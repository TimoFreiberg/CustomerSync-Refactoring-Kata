mod customer;
mod data_access;
mod external;

use data_access::{CustomerDataAccess, CustomerMatches};

pub use customer::{Address, Customer, CustomerType, ShoppingList};
pub use data_access::CustomerDataLayer;
pub use external::ExternalCustomer;

pub struct CustomerSync<Db> {
    customer_data_access: CustomerDataAccess<Db>,
}

impl<Db: CustomerDataLayer> CustomerSync<Db> {
    pub fn new(db: Db) -> Self {
        CustomerSync {
            customer_data_access: CustomerDataAccess::new(db),
        }
    }

    pub fn sync_with_data_layer(
        &mut self,
        external_customer: ExternalCustomer,
    ) -> Result<bool, String> {
        let customer_matches = if external_customer.is_company() {
            self.load_company(&external_customer)?
        } else {
            self.load_person(&external_customer)?
        };
        let mut customer = customer_matches.get_customer().clone();

        if customer.is_none() {
            let mut new_customer = Customer::new();
            new_customer.external_id = external_customer.external_id.clone();
            new_customer.master_external_id = external_customer.external_id.clone();

            customer = Some(new_customer);
        }
        let mut customer = customer.unwrap();

        self.populate_fields(&external_customer, &mut customer);

        let created = if customer.internal_id.is_none() {
            customer = self.create_customer(customer);
            true
        } else {
            self.update_customer(customer.clone());
            false
        };

        self.update_contact_info(&external_customer, &mut customer);

        if customer_matches.has_duplicates() {
            for duplicate in customer_matches.get_duplicates() {
                self.update_duplicate(&external_customer, duplicate.clone());
            }
        }

        self.update_preferred_store(&external_customer, &mut customer);
        self.update_relations(&external_customer, &mut customer);

        Ok(created)
    }

    fn load_company(
        &self,
        external_customer: &ExternalCustomer,
    ) -> Result<CustomerMatches, String> {
        let external_id = external_customer.external_id.as_ref().unwrap();
        let company_number = &external_customer.company_number;

        let mut customer_matches = self
            .customer_data_access
            .load_company_customer(external_id, &company_number.clone().unwrap());

        if customer_matches.customer.is_some()
            && Some(CustomerType::Company)
                != customer_matches.customer.clone().unwrap().customer_type
        {
            return Err(format!(
                "Existing customer for externalCustomer {} already exists and is not a company",
                external_id
            ));
        }

        if Some(String::from("ExternalId")) == customer_matches.match_term {
            let customer_company_number = customer_matches.customer.clone().unwrap().company_number;
            if *company_number != customer_company_number {
                if let Some(customer) = &mut customer_matches.customer {
                    customer.master_external_id = None;
                }
                let customer = customer_matches.customer.take();
                customer_matches.add_duplicate(customer);
                customer_matches.match_term = None;
            }
        } else if Some(String::from("CompanyNumber")) == customer_matches.match_term {
            let customer_external_id = customer_matches
                .customer
                .as_ref()
                .and_then(|customer| customer.external_id.clone());
            if let Some(customer_external_id) = customer_external_id {
                if **external_id != customer_external_id {
                    return Err(format!("Existing customer for externalCustomer {} doesn't match external id {} instead found {}",
                        company_number.clone().unwrap(),
                        external_id,
                        customer_external_id));
                }
            }
            if let Some(customer) = &mut customer_matches.customer {
                customer.external_id = Some(external_id.clone());
                customer.master_external_id = Some(external_id.clone());
                customer_matches.add_duplicate(None);
            }
        }

        Ok(customer_matches)
    }
    fn load_person(&self, external_customer: &ExternalCustomer) -> Result<CustomerMatches, String> {
        let external_id = external_customer.external_id.clone().unwrap();

        let mut customer_matches = self.customer_data_access.load_person_customer(&external_id);

        if customer_matches.customer.is_some() {
            if Some(CustomerType::Person)
                != customer_matches.customer.clone().unwrap().customer_type
            {
                return Err(format!(
                    "Existing customer for externalCustomer {} already exists and is not a person",
                    external_id
                ));
            }

            if Some(String::from("ExternalId")) != customer_matches.match_term {
                if let Some(customer) = &mut customer_matches.customer {
                    customer.external_id = Some(external_id.clone());
                    customer.master_external_id = Some(external_id);
                }
            }
        }

        Ok(customer_matches)
    }

    fn create_customer(&mut self, customer: Customer) -> Customer {
        self.customer_data_access.create_customer_record(customer)
    }

    fn update_customer(&mut self, customer: Customer) {
        self.customer_data_access.update_customer_record(customer)
    }

    fn update_duplicate(
        &mut self,
        external_customer: &ExternalCustomer,
        duplicate: Option<Customer>,
    ) {
        let mut duplicate = match duplicate {
            None => {
                let mut duplicate = Customer::new();
                duplicate.external_id = external_customer.external_id.clone();
                duplicate.master_external_id = external_customer.external_id.clone();
                duplicate
            }
            Some(duplicate) => duplicate,
        };

        duplicate.name = external_customer.name.clone();
        if duplicate.internal_id.is_none() {
            self.create_customer(duplicate);
        } else {
            self.update_customer(duplicate);
        }
    }

    fn update_relations(&mut self, external_customer: &ExternalCustomer, customer: &mut Customer) {
        for consumer_shopping_list in &external_customer.shopping_lists {
            self.customer_data_access
                .update_shopping_list(customer, consumer_shopping_list.clone());
        }
    }

    fn populate_fields(&mut self, external_customer: &ExternalCustomer, customer: &mut Customer) {
        customer.name = external_customer.name.clone();
        if external_customer.is_company() {
            customer.company_number = external_customer.company_number.clone();
            customer.customer_type = Some(CustomerType::Company);
        } else {
            customer.customer_type = Some(CustomerType::Person);
        }
    }

    fn update_contact_info(
        &mut self,
        external_customer: &ExternalCustomer,
        customer: &mut Customer,
    ) {
        customer.address = external_customer.address.clone();
    }

    fn update_preferred_store(
        &mut self,
        external_customer: &ExternalCustomer,
        customer: &mut Customer,
    ) {
        customer.preferred_store = external_customer.preferred_store.clone();
    }
}
