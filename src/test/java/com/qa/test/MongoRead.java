package com.qa.test;

import java.util.ArrayList;
import java.util.Arrays;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoRead {
	
	public void getMongoData() {
		System.out.println("Connecting to mongo database");
		MongoClient mongodb = new MongoClient("127.0.0.1",27017);
		System.out.println("Mongo DB connected successfully");
		MongoDatabase db = mongodb.getDatabase("jarvis");
		AggregateIterable<Document> referencedata =  db.getCollection("referencedata").
						aggregate(Arrays.asList(new Document("$match",new Document("company.title","YURTURE").append("company.email","aureliagonzales@yurture.com")),
						new Document("$project",new Document("lastname",1).append("company", 1))));
		
		MongoCursor<Document> cursor = referencedata.cursor();
		boolean isDataPresent = cursor.hasNext(); 		
		System.out.println("isDataPresent: " + isDataPresent);	
		if(isDataPresent) {
			Document doc = cursor.next();
			System.out.println("Mongo Document: \n " + doc.toJson());
			String companyTitle = doc.getEmbedded(Arrays.asList("company", "location","lastname"), String.class);
			String lastname = doc.get("lastname",String.class);
			System.out.println("Company location country: " + companyTitle + ", Last name: " + lastname);
		}
		mongodb.close();
	}
	public void getMongoArrayData() {
		MongoClient mongodb = new MongoClient("127.0.0.1",27017);
		System.out.println("Mongo DB connected successfully");
		MongoDatabase db = mongodb.getDatabase("jarvis");
		
//		AggregateIterable<Document> referencedata =  db.getCollection("referencedata").
//				aggregate(Arrays.asList(new Document("$match",new Document("Actors.age",new Document("$gte",50))),
//				new Document("$project",new Document("Actor",new Document("$arrayElemAt",Arrays.asList("$Actors",2))))));
		
		AggregateIterable<Document> referencedata =  db.getCollection("referencedata").
				aggregate(Arrays.asList(new Document("$match",new Document("Actors.age",new Document("$gte",50))),
				new Document("$project",new Document("directors",new Document("$filter",new Document("input","$Actors")
						.append("as", "actor").append("cond",new Document("$eq",Arrays.asList("$$actor.age",62))))))));
		
		MongoCursor<Document> cursor = referencedata.cursor();
		boolean isDataPresent = cursor.hasNext(); 		
		System.out.println("isDataPresent: " + isDataPresent);	
		if(isDataPresent) {
			Document doc = cursor.next();
			System.out.println("Mongo Document: \n " + doc.toJson());
/*			String actorName = doc.getEmbedded(Arrays.asList("Actor", "name"), String.class);
//			String lastname = doc.get("lastname",String.class);
			System.out.println("Actor Name: " + actorName );*/
			Document array = (Document) doc.get("directors",ArrayList.class).get(0);
			String actorName = array.get("name",String.class);
			System.out.println("Actor Name: " + actorName );
		}
		mongodb.close();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MongoRead mongoreader = new MongoRead();
		mongoreader.getMongoData();
//		mongoreader.getMongoArrayData();
	}

}
