package com.example.sandanalysis

object Constants {
//    DB NAME
    const val DB_NAME="SAMPLES_DB"
//    DB_VERSION
    const val DB_VERSION=1
//    TABLE NAME
    const val TABLE_NAME="SAMPLES_INFO_TABLE"
//    COLUMNS OF TABLE
    const val C_ID="ID"
    const val C_INN="INN"
    const val C_LAT="LAT"
    const val C_LONG="LONG"
    const val C_IMAGE="IMAGE"
    const val C_ADD_TIMESTAMP="ADD_TIMESTAMP"
    const val C_UPDATED_TIMESTAMP="UPDATED_TIMESTAMP"
//    Create table query
    const val CREATE_TABLE=("CREATE TABLE " + TABLE_NAME +" ("
        + C_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
        + C_INN+" INTEGER, "
        + C_LAT+" REAL, "
        + C_LONG+" REAL, "
        + C_IMAGE+" TEXT, "
        + C_ADD_TIMESTAMP+" TEXT, "
        + C_UPDATED_TIMESTAMP+" TEXT"
        +"); ")

}