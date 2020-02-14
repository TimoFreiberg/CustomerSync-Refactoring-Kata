use std::iter::FromIterator;

#[derive(Clone, Default, PartialEq, Eq, PartialOrd, Ord, Debug)]
pub struct Customer {
    pub external_id: Option<String>,
    pub master_external_id: Option<String>,
    pub company_number: Option<String>,
    pub internal_id: Option<String>,
    pub name: Option<String>,
    pub customer_type: Option<CustomerType>,
    pub preferred_store: Option<String>,
    pub address: Option<Address>,
    pub shopping_lists: Vec<ShoppingList>,
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

#[derive(Clone, PartialEq, Eq, PartialOrd, Ord, Debug)]
pub struct Address {
    pub street: String,
    pub postal_code: String,
    pub city: String,
}

#[derive(Clone, PartialEq, Eq, Hash, PartialOrd, Ord, Debug)]
pub struct ShoppingList {
    products: Vec<String>,
}

impl ShoppingList {
    pub fn new(products: Vec<String>) -> Self {
        ShoppingList { products }
    }
}

#[derive(Clone, PartialEq, Eq, Ord, PartialOrd, Debug)]
pub enum CustomerType {
    Company,
    Person,
}
