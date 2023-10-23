package com.shyam.weather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class WeatherForecast
 */
@WebServlet("/weather-forecast")
public class WeatherForecast extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WeatherForecast() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		//read request form data
		String city = req.getParameter("inputCity");
		
		String location = city.replace(" ", "+");
		
		//weather API key
		String apiKey = "e045ee4550a0bc8ba9dd5d71f277a212";
		
		//weather API key URL
		String apiURL = "https://api.openweathermap.org/data/2.5/weather?q="+location+"&appid="+apiKey;
		
		try {
			//API integration
			@SuppressWarnings("deprecation")
			URL url = new URL(apiURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			
			//reading the data from network
			InputStream is = connection.getInputStream();
			InputStreamReader reader = new InputStreamReader(is);
			
			//Store in String format
			StringBuilder resContent = new StringBuilder();
			
			//Scan the input from the reader
			Scanner scn = new Scanner(reader);
			
			//read the data from scanner and append it to StringBuilder
			while(scn.hasNext()) {
				resContent.append(scn.nextLine());
			}
			
			//Parsing the data into Json
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(resContent.toString(), JsonObject.class);
			
			//get Date and Time
			long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
			String date = new Date(dateTimestamp).toString();
			
			//get Temperature
			double tempKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
			int tempCelcius = (int) (tempKelvin - 273.15);
			
			//get Humidity
			int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
			
			//get Wind speed
			double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
			
			//get Weather Condition
			String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
			
			//set all the received data on request attributes
			req.setAttribute("date", date);
			req.setAttribute("location", location);
			req.setAttribute("city", city);
			req.setAttribute("temperature", tempCelcius);
			req.setAttribute("weatherCondition", weatherCondition);
			req.setAttribute("humidity", humidity);
			req.setAttribute("windSpeed", windSpeed);
			req.setAttribute("weatherData", resContent.toString());
			
			
			//disconnect the Connection
			connection.disconnect();
			
			//Close the Scanner
			scn.close();
		}
		catch(IOException ie) {
			ie.printStackTrace();
		}
		
		
		//forwards the request and response to JSP page 
		req.getRequestDispatcher("weather-app.jsp").forward(req, res);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(req, res);
	}

}
