package com.psl.JsonComparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class JSONComparator {

	public static String OLD_JSON_FILE_PATH = "C:/Users/nikita_marne/Desktop/JSON-Schema Validation/Policy-Schema_old.json";
	public static String NEW_JSON_FILE_PATH = "C:/Users/nikita_marne/Desktop/JSON-Schema Validation/Policy-Schema_new.json";
	public static String RESULT_OUTPUT_FILE_PATH = "C:/Users/nikita_marne/Desktop/JSON-Schema Validation/Json_Parsing_Output.txt";
	public static Integer FOUND_OBJECT_COUNT =0;
	public static Integer MIS_MATCH_REQUIRED_FIELD_COUNT =0;
	public static List<JSONObject> mis_match_objects_new_json = new ArrayList<JSONObject>();
	public static List<JSONObject> mis_match_objects_old_json = new ArrayList<JSONObject>();
	public static List<JSONObject> notFoundObjects = new ArrayList<JSONObject>();
	public static List<String> mismatchingRequiredFieldsNewJSON = new ArrayList<String>();
	public static List<String> mismatchingRequiredFieldsOldJSON = new ArrayList<String>();
	public static List<String> requiredFieldInNewJson = new ArrayList<String>();
	public static List<String> requiredFieldInOldJson = new ArrayList<String>();
	
	
	public static void main(String[] args) {
		
		jsonComparator();
	}
	
	public static void jsonComparator(){
		File oldFile = new File(OLD_JSON_FILE_PATH);
		File newFile = new File(NEW_JSON_FILE_PATH);
		File resultFile = new File(RESULT_OUTPUT_FILE_PATH);
		JSONParser parser = new JSONParser();
		
		try {
			Object obj = parser.parse(new FileReader(oldFile));
			JSONObject jsonObject = (JSONObject) obj;
			System.out.println(jsonObject);
			loopThroughOldJson(jsonObject);
			PrintWriter writer = new PrintWriter(resultFile);
			
			
			System.out.println("\n======================== //******* MisMatched Objects Are *********** \\ ==========================");
			System.out.println("\n******************* MIS-MATCHED-OBJECTS-IN-NEW-JSON******************");
			System.out.println(mis_match_objects_new_json);
			System.out.println("\n******************* MIS-MATCHED-OBJECTS-IN-OLD-JSON******************");
			System.out.println(mis_match_objects_old_json);
			System.out.println("\n******************* OBJECTS NOT FOUND IN NEW JSON ******************");
			System.out.println(notFoundObjects);
			System.out.println("\n*******************COUNT OF FOUND OBJECTS******************");
			System.out.println(FOUND_OBJECT_COUNT);
			System.out.println("\n******************* MIS-MATCHED-REQUIRED-FIELDS-IN-NEW-JSON******************");
			System.out.println(mismatchingRequiredFieldsNewJSON);
			System.out.println("\n******************* MIS-MATCHED-REQUIRED-FIELDS-IN-OLD-JSON******************");
			System.out.println(mismatchingRequiredFieldsOldJSON);
			System.out.println("\n*******************COUNT OF NOT-FOUND REQUIRED FIELDS IN BOTH JSONS******************");
			System.out.println(MIS_MATCH_REQUIRED_FIELD_COUNT);
			
			
			//Writing the output to a Text file
			writer.println("\n======================== //******* MisMatched Objects Are *********** \\ ==========================");
			writer.println();
			writer.println("\n******************* MIS-MATCHED-OBJECTS-IN-NEW-JSON******************");
			writeDataToFile(mis_match_objects_new_json, writer);
			writer.println();
			writer.println("\n******************* MIS-MATCHED-OBJECTS-IN-OLD-JSON******************");
			writeDataToFile(mis_match_objects_old_json, writer);
			writer.println();
			writer.println("\n******************* OBJECTS NOT FOUND IN NEW JSON ******************");
			writeDataToFile(notFoundObjects,writer);
			writer.println();
			writer.println("\n*******************COUNT OF FOUND OBJECTS******************");
			
			if(FOUND_OBJECT_COUNT == 0)
				writer.println("Sorry , Nothing to show..!!");
			else
				writer.println(FOUND_OBJECT_COUNT);
			
			writer.println();
			writer.println("\n******************* MIS-MATCHED-REQUIRED-FIELDS-IN-NEW-JSON******************");
			writeDataToFile(mismatchingRequiredFieldsNewJSON,writer);
			writer.println();
			writer.println("\n******************* MIS-MATCHED-REQUIRED-FIELDS-IN-OLD-JSON******************");
			writeDataToFile(mismatchingRequiredFieldsOldJSON,writer);
			writer.println();
			writer.println("\n*******************COUNT OF NOT-FOUND REQUIRED FIELDS IN BOTH JSONS******************");
			
			if(MIS_MATCH_REQUIRED_FIELD_COUNT == 0)
				writer.println("Sorry , Nothing to show..!!");
			else
				writer.println(MIS_MATCH_REQUIRED_FIELD_COUNT);
			
			writer.println();
			writer.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public static Object loopThroughJSON(Object jObj,String keyToBeSearched)
	{

		JSONObject objectFound = null;
		
		if(jObj instanceof JSONObject)
		{
			List<String> keys = new ArrayList<String>(((JSONObject) jObj).keySet());
			//System.out.println(keys);
			Iterator<String> itr = keys.iterator();
			while(itr.hasNext())
			{
				String key = itr.next();
				//System.out.println("Key encountered is: "+key);
				Object value = ((JSONObject) jObj).get(key);
				//System.out.println("Value type is: " +value.getClass().getSimpleName());
				String datatype = value.getClass().getSimpleName();
				
				if(key.equalsIgnoreCase(keyToBeSearched))
				{
					objectFound = new JSONObject();
					objectFound.put(key, value);
					return objectFound;
				}
				if(datatype.equalsIgnoreCase("String"))
				{
					//System.err.println("------[ key = "+key+" Value = "+value+" ]-------");
				}
				else if(((JSONObject) jObj).get(key) instanceof JSONArray)
				{
					//System.err.println("It is an array..!!!");
					JSONArray jsonArray = (JSONArray) ((JSONObject) jObj).get(key); 
					loopThroughJSON(jsonArray, keyToBeSearched);
				}
				else if(!(key.equalsIgnoreCase(keyToBeSearched)))
				{
					if(((JSONObject) jObj).get(key) instanceof JSONObject && !(key.equalsIgnoreCase(keyToBeSearched)))
					{
						Object result = loopThroughJSON(((JSONObject) jObj).get(key), keyToBeSearched);
						if(result != null)
							return result;
						
					}	   
				}
			}
		}
		else if(jObj instanceof JSONArray)
		{
			for(int i=0;i<((JSONArray)jObj).size();i++)
			{
				if(((JSONArray)jObj).get(i) instanceof String)
				{
					//System.out.println("Value of array is: "+((JSONArray)jObj).get(i));
				}
			}
		}
		return objectFound;
		
		
	}
	
	public static void loopThroughOldJson(Object oldJsonObject)
	{
		if(oldJsonObject instanceof JSONObject)
		{
			List<String> keys = new ArrayList<String>(((JSONObject) oldJsonObject).keySet());
			Iterator<String> oldJsonItr = keys.iterator();
			while(oldJsonItr.hasNext())
			{
				String key = oldJsonItr.next();
				Object value = ((JSONObject) oldJsonObject).get(key);
				String datatype = value.getClass().getSimpleName();
				
				if(datatype.equalsIgnoreCase("String"))
				{
					System.err.println("------[ key = "+key+" Value = "+value+" ]-------");
					JSONObject objectToBeSearched = new JSONObject();
					objectToBeSearched.put(key, value);
					loopThroughNewJson(objectToBeSearched);
				}
				else if(key.equalsIgnoreCase("properties"))
				{
					//System.out.println("Object is: "+value.toString());
					JSONObject propertiesJsonObject = (JSONObject) value;
					List<String> properties_keys_list = new ArrayList<String>(propertiesJsonObject.keySet());
					System.out.println("Properties Object size === "+properties_keys_list.size()+"------------------");
					Iterator<String> properties_array_itr = properties_keys_list.iterator();
					
					while(properties_array_itr.hasNext())
					{
						String properties_key = properties_array_itr.next();
						Object properties_value = propertiesJsonObject.get(properties_key);
						JSONObject objectToBeSearched = new JSONObject();
						objectToBeSearched.put(properties_key, properties_value);
						System.out.println("================ Object to be searched --========== : "+objectToBeSearched.toJSONString());
						loopThroughNewJson(objectToBeSearched);
					}
	
				}
				else if(key.equalsIgnoreCase("required"))
				{
					File newFile = new File(NEW_JSON_FILE_PATH);
					JSONParser parser = new JSONParser();
					Object obj;
					JSONObject newJsonObject = null;
					try {
						obj = parser.parse(new FileReader(newFile));
						newJsonObject = (JSONObject) obj;
						getRequiredType(newJsonObject);
						System.out.println("************** Required arraylist  in New JSON *****************: ");
						Iterator itr = requiredFieldInNewJson.iterator();
						while(itr.hasNext())
						{
							System.out.println("name = "+itr.next());
						}
						System.out.println("Required arraylist new size is: "+requiredFieldInNewJson.size());
						
						System.out.println("************** Required arraylist in Old Json  *****************: ");
						JSONArray jsonArray = (JSONArray) ((JSONObject) oldJsonObject).get(key); 
						for(int i=0;i<jsonArray.size();i++)
						{
							if(jsonArray.get(i) instanceof String)
							{
								System.out.println("name is = "+jsonArray.get(i));
								requiredFieldInOldJson.add((String) jsonArray.get(i));
							}
						}
						System.out.println("Required arraylist old size is: "+jsonArray.size());
						
						//compare the required Arraylists and find the mis-matching elements
						CompareRequiredArrayLists(requiredFieldInOldJson, requiredFieldInNewJson);
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		
			}
		}
	}
	
	public static void loopThroughNewJson(Object objectToBeSearched)
	{
		String keyToBeSearched = null ;
		File newFile = new File(NEW_JSON_FILE_PATH);
		JSONParser parser = new JSONParser();
		Object obj;
		JSONObject newJsonObject = null;
		try {
			obj = parser.parse(new FileReader(newFile));
			newJsonObject = (JSONObject) obj;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(objectToBeSearched instanceof JSONObject)
		{	
			List<String> key =  new ArrayList<String>(((JSONObject) objectToBeSearched).keySet());
			Iterator<String> keyIterator = key.iterator();
			while(keyIterator.hasNext())
			{
				keyToBeSearched = keyIterator.next();
				System.out.println("Key to be searched is: "+keyToBeSearched);
			}
			
			Object encounteredObject = loopThroughJSON(newJsonObject, keyToBeSearched);
			if(encounteredObject != null)
			{
				System.err.println("\n\nReceived object is: "+encounteredObject.toString());
				boolean equalObject = compareTwoJsonObjects(encounteredObject, objectToBeSearched);
				if(equalObject){
					System.err.println("Object found is matching.... proceed..!!");
					FOUND_OBJECT_COUNT++;
				}	
				else
					System.err.println("Object found is NOT matching....wait..!!");	
			
			}
			else
			{
				System.err.println("\n\nObject not found..!!");
				notFoundObjects.add((JSONObject) objectToBeSearched);
			}
			
		}
		
	}
	
	public static boolean compareTwoJsonObjects(Object obj1 , Object obj2)
	{
		boolean match = false;
		String json1 = ((JSONObject) obj1).toJSONString();
		String json2 = ((JSONObject) obj2).toJSONString();
		
		JsonParser parser = new JsonParser();
		JsonElement t1 = parser.parse(json1);
		JsonElement t2 = parser.parse(json2);

		match = t2.equals(t1);
		
		if(match == false)
		{
			mis_match_objects_new_json.add((JSONObject) obj1);
			mis_match_objects_old_json.add((JSONObject) obj2);
		}
		return match;
		
	}
	
	public static void getRequiredType(Object jObj)
	{
		
		if(jObj instanceof JSONObject)
		{
			List<String> keys = new ArrayList<String>(((JSONObject) jObj).keySet());
			//System.out.println(keys);
			Iterator<String> itr = keys.iterator();
			while(itr.hasNext())
			{
				String key = itr.next();
				Object value = ((JSONObject) jObj).get(key);
				String datatype = value.getClass().getSimpleName();
				
				if(datatype.equalsIgnoreCase("String"))
				{
					//System.err.println("------[ key = "+key+" Value = "+value+" ]-------");
				}
				else if(((JSONObject) jObj).get(key) instanceof JSONArray)
				{
					if(key.equalsIgnoreCase("required"))
					{
						JSONArray jsonArray = (JSONArray) ((JSONObject) jObj).get(key); 
						
						for(int i=0;i<jsonArray.size();i++)
						{
							if((jsonArray.get(i) instanceof String))
							{
								requiredFieldInNewJson.add((String) (jsonArray.get(i)));
							}
						}
					}
					else
					{
						JSONArray jsonArray = (JSONArray) ((JSONObject) jObj).get(key); 
						getRequiredType(jsonArray);
					}
					
					
				}
				else if(((JSONObject) jObj).get(key) instanceof JSONObject)
				{
					getRequiredType(((JSONObject) jObj).get(key));

				}	   
				
			}
		}
		else if(jObj instanceof JSONArray)
		{
			/*List<String> key =  new ArrayList<String>(((JSONObject) jObj).keySet());
			Iterator<String> keyIterator = key.iterator();
			String keyName = null;
			
			while(keyIterator.hasNext())
			{
				keyName = keyIterator.next();
				System.out.println("Key to be searched is: "+keyName);
			}
			*/
			/*if(keyName.equalsIgnoreCase("required"))
			{*/
				for(int i=0;i<((JSONArray)jObj).size();i++)
				{
					if(((JSONArray)jObj).get(i) instanceof String)
					{
						//requiredFieldInNewJson.add((String) ((JSONArray)jObj).get(i));
					}
				}
			//}
			
		}
		
		
	}
	
	public boolean keyExists(JSONObject  object, String searchedKey) {
	    boolean exists = object.containsKey(searchedKey);
	    if(!exists) {      
	        Iterator<?> keys = (Iterator<?>) object.keySet();
	        while( keys.hasNext() ) {
	            String key = (String)keys.next();
	            if ( object.get(key) instanceof JSONObject ) {
	                    exists = keyExists((JSONObject) object.get(key), searchedKey);
	            }
	        }
	    }
	    return exists;
	}

	public static void CompareRequiredArrayLists(List<String> oldJsonList,List<String> newJsonList)
	{
	    List<String> listOne = new ArrayList<>(oldJsonList);
	    List<String> listTwo = new ArrayList<>(newJsonList);
	        
	    listOne.removeAll(listTwo);
		mismatchingRequiredFieldsOldJSON.addAll(listOne);
		MIS_MATCH_REQUIRED_FIELD_COUNT+=mismatchingRequiredFieldsOldJSON.size();
		
		newJsonList.removeAll(oldJsonList);
		mismatchingRequiredFieldsNewJSON.addAll(newJsonList);
		MIS_MATCH_REQUIRED_FIELD_COUNT+=mismatchingRequiredFieldsNewJSON.size();
	}
	
	public static void writeDataToFile(List listToBeWritten ,PrintWriter writer)
	{
		if(listToBeWritten.size() == 0)
		{
			writer.println(" Sorry Nothing to show..!!");
		}
		for (Object obj : listToBeWritten) {
			writer.println(obj.toString());
		}
		
	}
	
}
