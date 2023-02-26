#!/bin/bash

##NOTE: -You need to specify the path of your "babawallet_db.sql" configuration file.
##      -Don't forget to be into root mode
DB_NAME="babawallet_db"
DB_TEST_NAME="babawallet_db_test"
readonly MYSQL=`which mysql`

$MYSQL -uroot -e "create database babawallet_db"
$MYSQL -uroot -e babawallet_db < babawallet_db.sql
echo "Database '${DB_NAME}' created successfully"
$MYSQL -uroot -e "create database babawallet_db_test"
$MYSQL -uroot babawallet_db_test < babawallet_db_test.sql
echo "Database '${DB_TEST_NAME}' created successfully"

