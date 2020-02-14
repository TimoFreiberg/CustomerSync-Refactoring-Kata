use std::collections::{HashMap, HashSet};

use customer_sync_refactoring_kata::{Customer, CustomerDataLayer, ShoppingList};

#[derive(Default)]
pub struct FakeDataBase {
    customers_by_external_id: HashMap<String, Customer>,
    customers_by_master_external_id: HashMap<String, Customer>,
    customers_by_company_number: HashMap<String, Customer>,
    shopping_lists: HashSet<ShoppingList>,
}

impl FakeDataBase {
    pub fn new() -> Self {
        Self::default()
    }
    pub fn add_customer(&mut self, customer: Customer) {
        if let Some(external_id) = customer.external_id.clone() {
            self.customers_by_external_id
                .insert(external_id, customer.clone());
        }
        if let Some(master_external_id) = customer.master_external_id.clone() {
            self.customers_by_master_external_id
                .insert(master_external_id, customer.clone());
        }
        if let Some(company_number) = customer.company_number.clone() {
            self.customers_by_company_number
                .insert(company_number, customer.clone());
        }
        if !customer.shopping_lists.is_empty() {
            self.shopping_lists.extend(customer.shopping_lists);
        }
    }
    pub fn customers(&self) -> Vec<&Customer> {
        let mut customers: Vec<_> = self
            .customers_by_master_external_id
            .values()
            .chain(self.customers_by_company_number.values())
            .chain(self.customers_by_external_id.values())
            .collect();
        customers.sort_by(|c1, c2| {
            c1.internal_id
                .cmp(&c2.internal_id)
                .then(c1.name.cmp(&c2.name))
        });
        customers.dedup();
        customers
    }

    pub fn shopping_lists(&self) -> &HashSet<ShoppingList> {
        &self.shopping_lists
    }

    pub fn print_contents(&self) -> String {
        let mut s = String::new();
        s.push_str("Fake Database.\nAll Customers {\n");
        for customer in self.customers() {
            s.push_str(&format!("{:#?}", customer));
            s.push('\n');
        }

        s.push('\n');
        s.push_str("\nAll Shopping Lists\n");
        let sorted_shopping_lists = {
            let mut shopping_lists: Vec<_> = self.shopping_lists.iter().cloned().collect();
            shopping_lists.sort_by_key(|shopping_list| format!("{:?}", shopping_list));
            shopping_lists
        };
        s.push_str(&format!("{:#?}", sorted_shopping_lists));

        s
    }
}

impl CustomerDataLayer for &mut FakeDataBase {
    fn find_by_master_external_id(&self, master_external_id: &str) -> Option<Customer> {
        self.customers_by_master_external_id
            .get(master_external_id)
            .cloned()
    }
    fn find_by_company_number(&self, company_number: &str) -> Option<Customer> {
        self.customers_by_company_number
            .get(company_number)
            .cloned()
    }
    fn find_by_external_id(&self, external_id: &str) -> Option<Customer> {
        self.customers_by_external_id.get(external_id).cloned()
    }

    fn create_customer_record(&mut self, mut customer: Customer) -> Customer {
        customer.internal_id = Some(String::from("fake internalId"));
        self.add_customer(customer.clone());
        customer
    }

    fn update_customer_record(&mut self, customer: Customer) {
        self.add_customer(customer);
    }

    fn update_shopping_list(&mut self, shopping_list: ShoppingList) {
        self.shopping_lists.insert(shopping_list);
    }
}
