use crate::customer::{Address, ShoppingList};

#[derive(Default)]
pub struct ExternalCustomer {
    pub address: Option<Address>,
    pub name: Option<String>,
    pub preferred_store: Option<String>,
    pub shopping_lists: Vec<ShoppingList>,
    pub external_id: Option<String>,
    pub company_number: Option<String>,
}

impl ExternalCustomer {
    pub fn new() -> Self {
        Self::default()
    }
    pub fn is_company(&self) -> bool {
        self.company_number != None
    }
}
