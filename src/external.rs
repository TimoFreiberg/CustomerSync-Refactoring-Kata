use crate::customer::{Address, ShoppingList};

#[derive(Default, Clone, Debug)]
pub struct ExternalCustomer {
    pub external_id: Option<String>,
    pub company_number: Option<String>,
    pub name: Option<String>,
    pub preferred_store: Option<String>,
    pub address: Option<Address>,
    pub shopping_lists: Vec<ShoppingList>,
}

impl ExternalCustomer {
    pub fn new() -> Self {
        Self::default()
    }
    pub fn is_company(&self) -> bool {
        self.company_number != None
    }
}
