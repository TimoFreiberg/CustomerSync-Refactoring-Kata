use crate::customer::{Customer, ShoppingList};

pub struct CustomerDataAccess<Db> {
    customer_data_layer: Db,
}

impl<Db: CustomerDataLayer> CustomerDataAccess<Db> {
    pub fn new(db: Db) -> Self {
        CustomerDataAccess {
            customer_data_layer: db,
        }
    }

    pub fn create_customer_record(&mut self, customer: Customer) -> Customer {
        self.customer_data_layer.create_customer_record(customer)
    }

    pub fn update_customer_record(&mut self, customer: Customer) {
        self.customer_data_layer.update_customer_record(customer);
    }

    pub fn update_shopping_list(
        &mut self,
        customer: &mut Customer,
        consumer_shopping_list: ShoppingList,
    ) {
        customer.add_shopping_list(consumer_shopping_list.clone());
        self.customer_data_layer
            .update_shopping_list(consumer_shopping_list);
        self.customer_data_layer
            .update_customer_record(customer.clone());
    }

    pub fn load_company_customer(
        &self,
        external_id: &str,
        company_number: &str,
    ) -> CustomerMatches {
        let mut matches = CustomerMatches::new();
        let match_by_external_id = self.customer_data_layer.find_by_external_id(external_id);
        if match_by_external_id.is_some() {
            matches.customer = match_by_external_id;
            matches.match_term = Some(String::from("ExternalId"));
            let match_by_master_id = self
                .customer_data_layer
                .find_by_master_external_id(external_id);
            if match_by_master_id.is_some() {
                matches.add_duplicate(match_by_master_id);
            }
        } else {
            let match_by_company_number = self
                .customer_data_layer
                .find_by_company_number(company_number);
            if match_by_company_number.is_some() {
                matches.customer = match_by_company_number;
                matches.match_term = Some(String::from("CompanyNumber"));
            }
        }

        matches
    }

    pub fn load_person_customer(&self, external_id: &str) -> CustomerMatches {
        let mut matches = CustomerMatches::new();
        let match_by_personal_number = self.customer_data_layer.find_by_external_id(external_id);
        matches.customer = match_by_personal_number.clone();
        if match_by_personal_number.is_some() {
            matches.match_term = Some(String::from("ExternalId"));
        }
        matches
    }
}

pub trait CustomerDataLayer {
    fn create_customer_record(&mut self, customer: Customer) -> Customer;
    fn update_customer_record(&mut self, customer: Customer);
    fn update_shopping_list(&mut self, consumer_shopping_list: ShoppingList);
    fn find_by_external_id(&self, external_id: &str) -> Option<Customer>;
    fn find_by_master_external_id(&self, external_id: &str) -> Option<Customer>;
    fn find_by_company_number(&self, company_number: &str) -> Option<Customer>;
}

pub struct CustomerMatches {
    pub customer: Option<Customer>,
    pub match_term: Option<String>,
    duplicates: Vec<Option<Customer>>,
}

impl CustomerMatches {
    pub fn new() -> Self {
        CustomerMatches {
            customer: None,
            match_term: None,
            duplicates: Vec::new(),
        }
    }
    pub fn get_customer(&self) -> &Option<Customer> {
        &self.customer
    }
    pub fn has_duplicates(&self) -> bool {
        self.duplicates.is_empty()
    }
    pub fn get_duplicates(&self) -> &[Option<Customer>] {
        &self.duplicates
    }
    pub fn add_duplicate(&mut self, customer: Option<Customer>) {
        self.duplicates.push(customer);
    }
}
