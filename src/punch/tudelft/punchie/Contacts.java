package punch.tudelft.punchie;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;

public class Contacts {
	
	private Activity mainActivity;
	private ContentResolver cr;
	private String selectPhone = CommonDataKinds.Phone.CONTACT_ID + " = ?";
	private String selectEmail = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?";
	
	public Contacts(Activity activity) {
		mainActivity = activity;
		cr = mainActivity.getContentResolver();
	}
	
	public void updateContact(PunchMember member, PhoneContact contact, Boolean updatePhone, Boolean updateEmail) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		if(updatePhone) {
			String[] phoneArgs = new String[]{contact.getContactId() + ""};
			ops.add(ContentProviderOperation.newUpdate(CommonDataKinds.Phone.CONTENT_URI)
	                .withSelection(selectPhone, phoneArgs)
	                .withValue(CommonDataKinds.Phone.NUMBER, member.getMobileNr())
	                .build());
		}
		
		if(updateEmail) {
			String[] emailArgs = new String[]{contact.getContactId() + ""};
			ops.add(ContentProviderOperation.newUpdate(ContactsContract.CommonDataKinds.Email.CONTENT_URI)
	                .withSelection(selectEmail, emailArgs)
	                .withValue(ContactsContract.CommonDataKinds.Email.DATA, member.getEmail())
	                .build());
		}
		if(ops.size() > 0) {
			try {
				cr.applyBatch(ContactsContract.AUTHORITY, ops);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addContact(PunchMember member) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		 ops.add(ContentProviderOperation.newInsert(
		 ContactsContract.RawContacts.CONTENT_URI)
		     .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
		     .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
		     .build());

		 //------------------------------------------------------ Names
		 if (member.getName() != null) {
		     ops.add(ContentProviderOperation.newInsert(
		     ContactsContract.Data.CONTENT_URI)
		         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
		         .withValue(ContactsContract.Data.MIMETYPE,
		     ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
		         .withValue(
		     ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
		     member.getName()).build());
		 }

		 //------------------------------------------------------ Mobile Number                     
		 if (member.getMobileNr() != null) {
		     ops.add(ContentProviderOperation.
		     newInsert(ContactsContract.Data.CONTENT_URI)
		         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
		         .withValue(ContactsContract.Data.MIMETYPE,
		     ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
		         .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, member.getMobileNr())
		         .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
		     ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
		         .build());
		 }

		 //------------------------------------------------------ Email
		 if (member.getEmail() != null) {
		     ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
		         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
		         .withValue(ContactsContract.Data.MIMETYPE,
		     ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
		         .withValue(ContactsContract.CommonDataKinds.Email.DATA, member.getEmail())
		         .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)
		         .build());
		 }

		 // Asking the Contact provider to create a new contact                 
		 try {
		     cr.applyBatch(ContactsContract.AUTHORITY, ops);
		 } catch (Exception e) {
		     e.printStackTrace();
		 } 
	}
	
	public PhoneContact getContact(PunchMember member) {
		String[] nameParts = member.getName().split(" ");
		String name = nameParts[0] + " " + nameParts[nameParts.length-1];
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null,ContactsContract.PhoneLookup.DISPLAY_NAME + " = ?", new String[]{name}, null);
        
        String id = null;
        String displayName = null;
        ArrayList<String> phones = null;
        ArrayList<String> emails = null;
        
        while (cur.moveToNext()) {
        	id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
        	displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            phones = getPhoneNumbers(id); 
            emails = getEmails(id);
      	  	break;
        }
        
        if(id != null && displayName != null) {
        	if(phones == null) {
        		phones = new ArrayList<String>();
        	}
        	if(emails == null) {
        		emails = new ArrayList<String>();
        	}
        	
        	return new PhoneContact(id, displayName, phones, emails);
        } else {
        	return null;
        }
	}
	
	private ArrayList<String> getPhoneNumbers(String id) {
		ArrayList<String> phones = new ArrayList<String>();

		Cursor cursor = cr.query(
		        CommonDataKinds.Phone.CONTENT_URI, 
		        null, 
		        CommonDataKinds.Phone.CONTACT_ID +" = ?", 
		        new String[]{id}, null);

		while (cursor.moveToNext()) 
		{
		    phones.add(cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER)));
		} 

		cursor.close();
		
		return phones;
	}
	
	private ArrayList<String> getEmails(String id) {
		ArrayList<String> emails = new ArrayList<String>();
		
		Cursor cursor = cr.query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
		        null, 
		        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
		        new String[]{id}, null);

		while (cursor.moveToNext()) 
		{
		    emails.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
		} 

		cursor.close();
		
		return emails;
	}
}
