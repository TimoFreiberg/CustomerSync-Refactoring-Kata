use crate::customer::{Customer, ShoppingList};

pub struct CustomerDataAccess<Db> {
    customer_data_layer: Db,
}

impl<Db: CustomerDataLayer> CustomerDataAccess<Db> {
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
}

pub trait CustomerDataLayer {
    fn create_customer_record(&mut self, customer: Customer) -> Customer;
    fn update_customer_record(&mut self, customer: Customer);
    fn update_shopping_list(&mut self, consumer_shopping_list: ShoppingList);
}

pub struct CustomerMatches {}

impl CustomerMatches {
    pub fn get_customer(&self) -> Option<Customer> {
        None
    }
    pub fn has_duplicates(&self) -> bool {
        true
    }
    pub fn get_duplicates(&self) -> Vec<Option<Customer>> {
        vec![]
    }
}
