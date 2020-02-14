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

    let mut to_assert = print_before_state(&external_customer, &db);

    let mut sut = CustomerSync::new(&mut db);

    let created = sut.sync_with_data_layer(external_customer).unwrap();

    assert!(!created);
    print_after_state(&db, &mut to_assert);
    insta::assert_snapshot!(to_assert);
}

#[test]
fn sync_private_person_by_external_id() {
    let external_id = String::from("12345");

    let mut external_customer = create_external_private_person();
    external_customer.external_id = Some(external_id.clone());

    let mut customer = Customer::new();
    customer.customer_type = Some(CustomerType::Person);
    customer.internal_id = Some(String::from("67576"));
    customer.external_id = Some(external_id);

    let mut db = FakeDataBase::new();
    db.add_customer(customer);

    let mut to_assert = print_before_state(&external_customer, &db);

    let mut sut = CustomerSync::new(&mut db);

    let created = sut.sync_with_data_layer(external_customer).unwrap();

    assert!(!created);
    print_after_state(&db, &mut to_assert);
    insta::assert_snapshot!(to_assert);
}

#[test]
fn sync_shopping_lists() {
    let external_id = String::from("12345");

    let mut external_customer = create_external_company();
    external_customer.external_id = Some(external_id.clone());

    let mut customer = create_customer_with_same_company_as(&external_customer);
    customer.external_id = Some(external_id);
    customer.shopping_lists = vec![ShoppingList::new(vec![
        String::from("eyeliner"),
        String::from("blusher"),
    ])];

    let mut db = FakeDataBase::new();
    db.add_customer(customer);

    let mut to_assert = print_before_state(&external_customer, &db);

    let mut sut = CustomerSync::new(&mut db);

    let created = sut.sync_with_data_layer(external_customer).unwrap();

    assert!(!created);
    print_after_state(&db, &mut to_assert);
    insta::assert_snapshot!(to_assert);
}

#[test]
fn sync_new_company_customer() {
    let mut external_customer = create_external_company();
    external_customer.external_id = Some(String::from("12345"));

    let mut db = FakeDataBase::new();

    let mut to_assert = print_before_state(&external_customer, &db);

    let mut sut = CustomerSync::new(&mut db);

    let created = sut.sync_with_data_layer(external_customer).unwrap();

    assert!(created);
    print_after_state(&db, &mut to_assert);
    insta::assert_snapshot!(to_assert);
}

#[test]
fn sync_new_private_customer() {
    let mut external_customer = create_external_private_person();
    external_customer.external_id = Some(String::from("12345"));

    let mut db = FakeDataBase::new();

    let mut to_assert = print_before_state(&external_customer, &db);

    let mut sut = CustomerSync::new(&mut db);

    let created = sut.sync_with_data_layer(external_customer).unwrap();

    assert!(created);
    print_after_state(&db, &mut to_assert);
    insta::assert_snapshot!(to_assert);
}

#[test]
fn conflict_exception_when_existing_customer_is_person() {
    let external_id = String::from("12345");

    let mut external_customer = create_external_company();
    external_customer.external_id = Some(external_id.clone());

    let mut customer = Customer::new();
    customer.customer_type = Some(CustomerType::Person);
    customer.internal_id = Some(String::from("45435"));
    customer.external_id = Some(external_id);

    let mut db = FakeDataBase::new();
    db.add_customer(customer);

    let mut to_assert = print_before_state(&external_customer, &db);

    let mut sut = CustomerSync::new(&mut db);

    let err = sut.sync_with_data_layer(external_customer);

    assert!(err.is_err());
    print_after_state(&db, &mut to_assert);
    insta::assert_snapshot!(to_assert);
}

#[test]
fn sync_by_external_id_but_company_numbers_conflict() {
    let external_id = String::from("12345");

    let mut external_customer = create_external_company();
    external_customer.external_id = Some(external_id.clone());

    let mut customer = create_customer_with_same_company_as(&external_customer);
    customer.external_id = Some(external_id);
    customer.company_number = Some(String::from("000-3234"));

    let mut db = FakeDataBase::new();
    db.add_customer(customer);

    let mut to_assert = print_before_state(&external_customer, &db);

    let mut sut = CustomerSync::new(&mut db);

    let created = sut.sync_with_data_layer(external_customer).unwrap();

    assert!(created);
    print_after_state(&db, &mut to_assert);
    insta::assert_snapshot!(to_assert);
}

#[test]
fn sync_by_company_number() {
    let company_number = String::from("12345");

    let mut external_customer = create_external_company();
    external_customer.company_number = Some(company_number.clone());

    let mut customer = create_customer_with_same_company_as(&external_customer);
    customer.company_number = Some(company_number);
    customer.shopping_lists = vec![ShoppingList::new(vec![
        String::from("eyeliner"),
        String::from("mascara"),
        String::from("blue bombe eyeshadow"),
    ])];

    let mut db = FakeDataBase::new();
    db.add_customer(customer);

    let mut to_assert = print_before_state(&external_customer, &db);

    let mut sut = CustomerSync::new(&mut db);

    let created = sut.sync_with_data_layer(external_customer).unwrap();

    assert!(!created);
    print_after_state(&db, &mut to_assert);
    insta::assert_snapshot!(to_assert);
}

#[test]
fn sync_by_company_number_with_conflicting_external_id() {
    let company_number = String::from("12345");

    let mut external_customer = create_external_company();
    external_customer.company_number = Some(company_number.clone());
    external_customer.external_id = Some(String::from("45646"));

    let mut customer = create_customer_with_same_company_as(&external_customer);
    customer.company_number = Some(company_number);
    customer.external_id = Some(String::from("conflicting id"));

    let mut db = FakeDataBase::new();
    db.add_customer(customer);

    let mut to_assert = print_before_state(&external_customer, &db);

    let mut sut = CustomerSync::new(&mut db);

    let err = sut.sync_with_data_layer(external_customer);

    assert!(err.is_err());
    print_after_state(&db, &mut to_assert);
    insta::assert_snapshot!(to_assert);
}

#[test]
fn conflict_exception_when_existing_customer_is_company() {
    let external_id = String::from("12345");

    let mut external_customer = create_external_private_person();
    external_customer.external_id = Some(external_id.clone());

    let mut customer = Customer::new();
    customer.customer_type = Some(CustomerType::Company);
    customer.company_number = Some(String::from("32423-342"));
    customer.internal_id = Some(String::from("45435"));
    customer.external_id = Some(external_id);

    let mut db = FakeDataBase::new();
    db.add_customer(customer);

    let mut to_assert = print_before_state(&external_customer, &db);

    let mut sut = CustomerSync::new(&mut db);

    let err = sut.sync_with_data_layer(external_customer);

    assert!(err.is_err());
    print_after_state(&db, &mut to_assert);
    insta::assert_snapshot!(to_assert);
}

#[test]
fn sync_company_by_external_id_with_non_matching_master_id() {
    let external_id = String::from("12345");

    let mut external_customer = create_external_company();
    external_customer.external_id = Some(external_id.clone());

    let mut customer = create_customer_with_same_company_as(&external_customer);
    customer.external_id = Some(external_id.clone());
    customer.name = Some(String::from("company 1"));

    let mut customer2 = Customer::new();
    customer2.company_number = external_customer.company_number.clone();
    customer2.customer_type = Some(CustomerType::Company);
    customer2.internal_id = Some(String::from("45435234"));
    customer2.master_external_id = Some(external_id);
    customer2.name = Some(String::from("company 2"));

    let mut db = FakeDataBase::new();
    db.add_customer(customer);
    db.add_customer(customer2);

    let mut to_assert = print_before_state(&external_customer, &db);

    let mut sut = CustomerSync::new(&mut db);

    let created = sut.sync_with_data_layer(external_customer).unwrap();

    assert!(!created);
    print_after_state(&db, &mut to_assert);
    insta::assert_snapshot!(to_assert);
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
    external_customer.shopping_lists = vec![ShoppingList::new(vec![
        String::from("lipstick"),
        String::from("blusher"),
    ])];
    external_customer
}

fn create_external_private_person() -> ExternalCustomer {
    let mut external_customer = ExternalCustomer::new();
    external_customer.external_id = Some(String::from("12345"));
    external_customer.name = Some(String::from("Joe Bloggs"));
    external_customer.address = Some(Address {
        street: String::from("123 main st"),
        city: String::from("Stockholm"),
        postal_code: String::from("SE-123 45"),
    });
    external_customer.preferred_store = Some(String::from("Nordstan"));
    external_customer.shopping_lists = vec![ShoppingList::new(vec![
        String::from("lipstick"),
        String::from("foundation"),
    ])];
    external_customer
}

fn create_customer_with_same_company_as(external_customer: &ExternalCustomer) -> Customer {
    let mut customer = Customer::new();
    customer.company_number = external_customer.company_number.clone();
    customer.customer_type = Some(CustomerType::Company);
    customer.internal_id = Some(String::from("45435"));

    customer
}

fn print_before_state(external_customer: &ExternalCustomer, db: &FakeDataBase) -> String {
    let mut to_assert = String::new();
    to_assert.push_str("BEFORE:\n");
    to_assert.push_str(&db.print_contents());

    to_assert.push_str("\nSYNCING THIS:\n");
    to_assert.push_str(&format!("{:#?}", external_customer));

    to_assert
}

fn print_after_state(db: &FakeDataBase, to_assert: &mut String) {
    to_assert.push_str("\nAFTER:\n");
    to_assert.push_str(&db.print_contents());
}
