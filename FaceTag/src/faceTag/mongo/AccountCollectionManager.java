package faceTag.mongo;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;

import faceTag.entities.Account;

public class AccountCollectionManager {
	private static DBCollection getAccountCollection() {
		try {
			MongoDBSingleton.getInstance();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		DBCollection coll = MongoDBSingleton.getDataBase().getCollection("Account");
		coll.setObjectClass(Account.class);
		return coll;
	}

	public static void addAccount(String username, String password, String name) throws MongoException {
		// Create a User entry, then create the account.
		ObjectId userID = UserCollectionManager.addUser(name);

		Account newAccount = new Account(userID, username, password);
		DBCollection coll = getAccountCollection();
		coll.insert(newAccount);

	}

	public static Account checkPassword(String username, String password) {
		// Create a User entry, then create the account.
		BasicDBObject query = new BasicDBObject("username", username).append("password", password);

		DBCollection coll = getAccountCollection();

		return (Account) coll.findOne(query);
		
	}
}
