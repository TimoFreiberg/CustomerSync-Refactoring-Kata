use crate::customer::{Address, ShoppingList};

pub struct ExternalCustomer {
    pub address: Address,
    pub name: String,
    pub preferred_store: String,
    pub shopping_lists: Vec<ShoppingList>,
    pub external_id: String,
    pub company_number: Option<String>,
}

impl ExternalCustomer {
    pub fn is_company(&self) -> bool {
        self.company_number != None
    }
}
