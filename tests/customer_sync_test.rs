use customer_sync_refactoring_kata::{
    Address, Customer, CustomerSync, CustomerType, ExternalCustomer, ShoppingList,
};

mod fake_db;

use fake_db::FakeDataBase;

#[test]
fn sync_company_by_external_id() {
    let external_id = String::from("12345");

    let mut external_customer = create_external_company();
    external_customer.external_id = Some(external_id.clone());

    let mut customer = create_customer_with_same_company_as(&external_customer);
    customer.external_id = Some(external_id);

    let mut db = FakeDataBase::new();
    db.add_customer(customer);

    let mut sut = CustomerSync::new(&mut db);

    let created = sut.sync_with_data_layer(external_customer).unwrap();

    assert!(!created);
    assert_eq!(
        db.customers()[0].name.clone(),
        Some(String::from("Acme Inc."))
    );
}

fn create_external_company() -> ExternalCustomer {
    let mut external_customer = ExternalCustomer::new();
    external_customer.external_id = Some(String::from("12345"));
    external_customer.name = Some(String::from("Acme Inc."));
    external_customer.address = Some(Address {
        street: String::from("123 main st"),
        city: String::from("Helsingborg"),
        postal_code: String::from("SE-123 45"),
    });
    external_customer.company_number = Some(String::from("470813-8895"));
    external_customer.shopping_lists = vec![ShoppingList {
        products: vec![String::from("lipstick"), String::from("blusher")],
    }];
    external_customer
}

fn create_customer_with_same_company_as(external_customer: &ExternalCustomer) -> Customer {
    let mut customer = Customer::new();
    customer.company_number = external_customer.company_number.clone();
    customer.customer_type = Some(CustomerType::Company);
    customer.internal_id = Some(String::from("45435"));

    customer
}
