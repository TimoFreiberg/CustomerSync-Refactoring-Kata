use std::iter::FromIterator;

#[derive(Clone, Default)]
pub struct Customer {
    pub external_id: Option<String>,
    pub master_external_id: Option<String>,
    pub name: Option<String>,
    pub company_number: Option<String>,
    pub customer_type: Option<CustomerType>,
    pub internal_id: Option<String>,
    pub address: Option<Address>,
    pub shopping_lists: Vec<ShoppingList>,
    pub preferred_store: Option<String>,
}

impl Customer {
    pub fn new() -> Customer {
        Customer::default()
    }
    pub fn add_shopping_list(&mut self, consumer_shopping_list: ShoppingList) {
        let mut new_list = Vec::from_iter(self.shopping_lists.drain(..));
        new_list.push(consumer_shopping_list);
        self.shopping_lists = new_list;
    }
}

#[derive(Clone)]
pub struct Address {
    pub street: String,
    pub city: String,
    pub postal_code: String,
}

#[derive(Clone)]
pub struct ShoppingList {
    products: Vec<String>,
}

#[derive(Clone, PartialEq)]
pub enum CustomerType {
    Company,
    Person,
}
