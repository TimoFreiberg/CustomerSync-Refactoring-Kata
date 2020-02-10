mod customer;
mod data_access;
mod external;

use customer::{Customer, CustomerType};
use data_access::{CustomerDataAccess, CustomerDataLayer, CustomerMatches};
use external::ExternalCustomer;

pub struct CustomerSync<Db> {
    customer_data_access: CustomerDataAccess<Db>,
}

impl<Db: CustomerDataLayer> CustomerSync<Db> {
    pub fn sync_with_data_layer(&mut self, external_customer: ExternalCustomer) -> bool {
        let customer_matches: CustomerMatches;
        if external_customer.is_company() {
            customer_matches = self.load_company(&external_customer);
        } else {
            customer_matches = self.load_person(&external_customer);
        }
        let mut customer = customer_matches.get_customer();

        if customer.is_none() {
            let mut new_customer = Customer::new();
            new_customer.external_id = Some(external_customer.external_id.clone());
            new_customer.master_external_id = Some(external_customer.external_id.clone());

            customer = Some(new_customer);
        }
        let mut customer = customer.unwrap();

        self.populate_fields(&external_customer, &mut customer);

        let mut created = false;
        if customer.internal_id.is_none() {
            customer = self.create_customer(customer);
            created = true;
        } else {
            self.update_customer(customer.clone());
        }

        self.update_contact_info(&external_customer, &mut customer);

        if customer_matches.has_duplicates() {
            for duplicate in customer_matches.get_duplicates() {
                self.update_duplicate(&external_customer, duplicate);
            }
        }

        self.update_relations(&external_customer, &mut customer);
        self.update_preferred_store(&external_customer, &mut customer);

        created
    }

    fn load_company(&self, external_customer: &ExternalCustomer) -> CustomerMatches {
        let external_id = &external_customer.external_id;
        let company_number = &external_customer.company_number;

        let customer_matches = self
            .customer_data_access
            .load_company_customer(external_id, &company_number.clone().unwrap());

        customer_matches
    }
    fn load_person(&self, external_customer: &ExternalCustomer) -> CustomerMatches {
        CustomerMatches::new()
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
                duplicate.external_id = Some(external_customer.external_id.clone());
                duplicate.master_external_id = Some(external_customer.external_id.clone());
                duplicate
            }
            Some(duplicate) => duplicate,
        };

        duplicate.name = Some(external_customer.name.clone());
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
        customer.name = Some(external_customer.name.clone());
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
        customer.address = Some(external_customer.address.clone());
    }

    fn update_preferred_store(
        &mut self,
        external_customer: &ExternalCustomer,
        customer: &mut Customer,
    ) {
        customer.preferred_store = Some(external_customer.preferred_store.clone());
    }
}
