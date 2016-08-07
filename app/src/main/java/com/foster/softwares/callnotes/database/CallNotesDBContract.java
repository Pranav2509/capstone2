package com.foster.softwares.callnotes.database;

/**
 * Created by Pranav on 24/07/16.
 */
public class CallNotesDBContract {

    public final static String CALL_NOTES_DB = "CallNotesDB";
    public final static int DATBASE_VERSION = 8;

    public final static String CALL_NOTES_TABLE = "call_notes";

    public final static String CALL_NOTES_COL1 = "contact_id";
    public final static String CALL_NOTES_COL2 = "contact_name";
    public final static String CALL_NOTES_COL3 = "contact_number";
    public final static String CALL_NOTES_COL4 = "contact_call_note";
    public final static String CALL_NOTES_COL6 = "contact_details";
    public final static String CALL_NOTES_COL7 = "contact_image";
    public final static String CALL_NOTES_COL8 = "date_of_adding_note";


    public final static String CREATE_TABLE = "Create Table ";

    public final static String CALL_NOTES_CREATE_TABLE_QUERY = CREATE_TABLE + CALL_NOTES_TABLE + "("+
            CALL_NOTES_COL1+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            CALL_NOTES_COL2+" TEXT NOT NULL,"+
            CALL_NOTES_COL3+" TEXT NOT NULL,"+
            CALL_NOTES_COL4+" TEXT NOT NULL,"+
            CALL_NOTES_COL6+" TEXT NOT NULL,"+
            CALL_NOTES_COL7+" TEXT NOT NULL,"+
            CALL_NOTES_COL8+" DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ");";
}
