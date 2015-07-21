package faceTag.mongo;

import java.net.UnknownHostException;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;

import faceTag.entities.Account;

public class AccountCollectionManager {
	private static MongoCollection<Account> getAccountCollection() {
		try {
			MongoDBSingleton.getInstance();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		MongoCollection<Account> coll = MongoDBSingleton.getDataBase().getCollection("Account")
				.withDocumentClass(Account.class);
		return coll;
	}

	public static void addAccount(String username, String password, String name) throws MongoWriteException {
		// Create a User entry, then create the account.
		ObjectId userID = UserCollectionManager.addUser(name);

		Account newAccount = new Account(userID, username, password);
		MongoCollection<Account> coll = getAccountCollection();

		coll.insertOne(newAccount);

	}

	public static Account checkPassword(String username, String password) {
		// Create a User entry, then create the account.
		BasicDBObject query = new BasicDBObject("username", username).append("password", password);

		MongoCollection<Account> coll = getAccountCollection();
		Account result = new Account();
		result.putAll(coll.find(query).first());
		return result;

	}
}
