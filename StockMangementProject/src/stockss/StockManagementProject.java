package stockss;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class User implements java.io.Serializable{
	
	String name,userid,password;
	double balance;
	Stocks refresh=new Stocks();
	transient Scanner sc= new Scanner(System.in);
	transient Scanner scl= new Scanner(System.in);
	ArrayList <MyStock >transactions=new ArrayList <MyStock >();
	ArrayList <MyStock >stocksBought=new ArrayList <MyStock >();
	ArrayList <MyStock >cart=new ArrayList <MyStock >();
	
	
	 User(String userid,String name,String password,double balance){
		 this.userid=userid;
		 this.name=name;
		 this.password=password;
		 this.balance=balance;
	 }
	 
	 public void resetPassword() {
		 
	   while(true) {
		   String newPass;
		   System.out.print("Enter the new password: ");
		   newPass=sc.next();
		   System.out.print("Reenter the new password: ");
		   if(newPass.equals(sc.next())){
			   System.out.println("Password reset successfull");
			   return;
		   }
		   System.out.println("Password entered do no match.Enter 1 for retry or 0 to exit");
		   int retry=sc.nextInt();
		   if(retry==0)
			   return;
	   }
	 }
	 void display_user()
	 {
	 	System.out.println("Username : "+userid );
	 	System.out.println("Name : "+name);
	 	System.out.println("Balance : "+balance);
	 }
	 
	 
	 public void addToCart(String stkname) {
		 
		 
		 Stockdata stock=refresh.getStock(stkname);
		 System.out.println("How many quantities you of the stock you want to buy");
		 int qty=sc.nextInt();
		 for(int i=0;i<cart.size();i++) {
			 if(cart.get(i).myStk.name.equals(stkname)==true) {
				cart.get(i).qty+=qty;
				return;
			 }
		 }
		  MyStock my_stock=new MyStock (stock,qty);
		  cart.add(my_stock);
		  
		 	 
	 } 
	
public void purchase(String stkname) {
		 
		 Stockdata stock=refresh.getStock(stkname);
		 System.out.println("How many quantities you of the stock you want to buy");
		 int qty=sc.nextInt();
		  MyStock my_stock=new MyStock (stock,qty);
		  
		  if(balance>=my_stock.myStk.LTP*my_stock.qty) {
			  System.out.println("Are you sure you want to confirm the order? Enter 1 if so else enter 0");
			  int proceed=sc.nextInt();
			  if(proceed==1) {
				  my_stock.bgtPrice=my_stock.myStk.LTP;
				  balance-=my_stock.myStk.LTP*my_stock.qty;
				  //stocksBought.add(my_stock);
				  int flag=0;
				  for(int i=0;i<stocksBought.size();i++) {
						 if(stocksBought.get(i).myStk.name.equals(stkname)) {
							 stocksBought.get(i).bgtPrice=(stocksBought.get(i).bgtPrice*stocksBought.get(i).qty+my_stock.myStk.LTP*my_stock.qty)/(stocksBought.get(i).qty+my_stock.qty);
							 stocksBought.get(i).qty=stocksBought.get(i).qty+qty;	
							 flag=1;
						 }
				  }
				  if(flag==0) {
					  stocksBought.add(my_stock); 
				  }
				  MyStock my_stock1 = new MyStock(my_stock.myStk,my_stock.qty);
				  my_stock1.bgtPrice=my_stock.bgtPrice;
				  transactions.add(my_stock1);
				  System.out.println("Your stock order placed successfully");
			  }
			  else {
				  return;
			  }
		  }
		  else {
			  System.out.println("Your balance is "+ balance+" which is insufficient so couln't place your stock order");
		  }
		  
		 
	 }
	 public void removeCart() {
		 int i=0;
		 if(cart.size()==0) {
			 System.out.println("Cart is empty already"); 
			 return;
		 }
		 displayCart();
		 System.out.println("Enter the name of the stock you want to delete");
		 String name=scl.nextLine();
		 for( i=0;i<cart.size();i++) {
			 if(cart.get(i).myStk.name.equals(name)==true) {
				 break;
			 }
		 }
		 if(i<cart.size()) {
			 cart.remove(i);
			 System.out.println("Removed from cart successfully");
		 }else {
			 System.out.println("Not found");
		 }
	 }
	
	 
	
	 public void purchaseCart() {
		 if(cart.size()==0) {
			 System.out.println("Nothing to purchase. Cart is empty");
		 }else {
			 updateStk();
			 int bill=0; 
			 for(int i=0;i<cart.size();i++)	 {
				 MyStock my_stock=cart.get(i);
				 bill+=my_stock.myStk.LTP*my_stock.qty;
			 }if(bill>balance) {
				 System.out.println("Your balance is insufficient in order to buy all the items in from your cart\n Do you still want to proceed to buy the items for which your balance is sufficient?\nEnter 1 for yes or else enter 0");
				 int conti=sc.nextInt();
				 if(conti==0) {
					 return;
				 }
			 }
			 int flag=0,last_ind=0;
			 for(int i=0;i<cart.size();i++) {
				 MyStock my_stock=cart.get(i);

				 if(balance>=my_stock.myStk.LTP*my_stock.qty) 
				 {
					 my_stock.bgtPrice=my_stock.myStk.LTP;
					 balance-=my_stock.myStk.LTP*my_stock.qty;
					 int flag1=0;
					  for(int j=0;j<stocksBought.size();j++) {
							 if(stocksBought.get(j).myStk.name.equals(my_stock.myStk.name)) {
								 stocksBought.get(j).bgtPrice=(stocksBought.get(j).bgtPrice*stocksBought.get(j).qty+my_stock.myStk.LTP*my_stock.qty)/(stocksBought.get(j).qty+my_stock.qty);
								 stocksBought.get(j).qty=stocksBought.get(j).qty+my_stock.qty;	
								 flag1=1;
							 }
					  }
					  if(flag1==0) {
						  stocksBought.add(my_stock); 
					  }
					  MyStock my_stock1 = new MyStock(my_stock.myStk,my_stock.qty);
					  my_stock1.bgtPrice=my_stock.bgtPrice;
					  transactions.add(my_stock1);
					 System.out.println("The stock "+ my_stock.myStk.name+ "order placed successfully");
					 last_ind=i;
				 }else {
					 System.out.println("Your balance, which is "+ balance+" is insufficient so couldn't place your stock order for stock "+ my_stock.myStk.name+ "and ahead");
					 flag=1;
					 break;
				 }
			 }if(flag==0) {
				 cart.clear();
				 System.out.println("All stocks from cart purchased");
			 }else {
				 for(int i=0;i<=last_ind;i++) {
					 cart.remove(i);
				 }
			 }
		 }
	 }
	 public void updateStk() {
		 int size=cart.size();
		 refresh.retrive();
		 for(int i=0;i<size;i++) {
			 Stockdata stock=refresh.getStock(cart.get(i).myStk.name);
			 cart.get(i).myStk=stock; 
		 }
		 size=stocksBought.size();
		 for(int i=0;i<size;i++) {
			 Stockdata stock=refresh.getStock(stocksBought.get(i).myStk.name);
			 stocksBought.get(i).myStk=stock;
			 
		 }
	 }
	 public void displayCart() {
		 updateStk();
		 if(cart.size()==0) {
			 System.out.println("\nSorry you don't have any items in your cart!");
			 return;
		 }
		 System.out.println("\n----------------------------------------------------CART----------------------------------------------------------");
		 System.out.printf("%15s %15s %15s\n","Stock Name","Quantity","LTP");
		 for(int i=0;i<cart.size();i++) {
			 System.out.printf("%15s %15s %15s\n",cart.get(i).myStk.name,cart.get(i).qty,cart.get(i).myStk.LTP);	 
		 }
		 System.out.println("--------------------------------------------------------------------------------------------------------------------");
	 }
	 public void displayBoughtstk() {
		 if(stocksBought.size()==0) {
			 System.out.println("\nSorry you haven't bought any stocks yet!");
			 return;
		 }
		 System.out.println("\n---------------------------------------------BOUGHT STOCKS--------------------------------------------------------");
		 System.out.printf("%15s %15s %15s\n","Stock Name","Quantity","Cost Price");
		 for(int i=0;i<stocksBought.size();i++) {
			 System.out.printf("%15s %15s %15s\n",stocksBought.get(i).myStk.name,stocksBought.get(i).qty,stocksBought.get(i).bgtPrice);	 
		 }
		 System.out.println("--------------------------------------------------------------------------------------------------------------------");
	 }
	 public void displaytransacs() {
		 if(transactions.size()==0) {
			 System.out.println("\nSorry you dont have any transaction history yet!");
			 return;
		 }
		 System.out.println("\n---------------------------------------------TRANSACTION HISTORY-----------------------------------------------------");
		 System.out.printf("%15s %15s %15s %15s %15s %15s %15s\n","Stock Name","Quantity","Cost Price","Bought/Sold","Selling Price","Profit","Loss");
		 for(int i=0;i<transactions.size();i++) {
			 if(transactions.get(i).soldPrice==0) {
				 System.out.printf("%15s %15s %15s %15s %15s %15s %15s\n",transactions.get(i).myStk.name,transactions.get(i).qty,transactions.get(i).bgtPrice,"bought","-  ","-  ","-  ");
			 }
			 else {
				 if(transactions.get(i).profit>=transactions.get(i).loss) {
					 System.out.printf("%15s %15s %15s %15s %15s %15s %15s\n",transactions.get(i).myStk.name,transactions.get(i).qty,transactions.get(i).bgtPrice,"sold",transactions.get(i).soldPrice,transactions.get(i).profit,"-  ");
				 }
				 else if(transactions.get(i).profit<=transactions.get(i).loss) {
					 System.out.printf("%15s %15s %15s %15s %15s %15s %15s\n",transactions.get(i).myStk.name,transactions.get(i).qty,transactions.get(i).bgtPrice,"sold",transactions.get(i).soldPrice,"-  ",transactions.get(i).loss);
				 }
			 } 
			    
		 }
		 System.out.println("--------------------------------------------------------------------------------------------------------------------");
	 }
	
	 public void sell() {
		 updateStk();
		 System.out.println("These are the stocks that you've bought : ");
		 this.displayBoughtstk();
		 String name1;
		 System.out.println("Enter name of stock you want to sell");
		 name1 = scl.nextLine();
		 int flag=0;
		 MyStock stk=null;
		 int tempQty;
		 for(int i=0 ; i<stocksBought.size();i++)
		 {
			if(stocksBought.get(i).myStk.name.equals(name1))
			{
				System.out.println("How many of "+name1+" stocks do you want to sell?");
				tempQty = sc.nextInt();
				while(true) {
					if(tempQty>stocksBought.get(i).qty) {
						System.out.println("Sorry you just have "+stocksBought.get(i).qty+" stocks of "+name1);
						System.out.println("Do you want to enter the quantity again? (1= yes and 0= no)");
						int ch = sc.nextInt();
						if(ch==0) {
							return;
						}
					}
					else {
						break;
					}
					System.out.println("How many of "+name1+" stocks do you want to sell?");
					tempQty = sc.nextInt();
				}
				
				float tempGain=0;
				tempGain = stocksBought.get(i).myStk.LTP*tempQty-stocksBought.get(i).bgtPrice*tempQty;
				if(tempGain<0) {
					System.out.println("If you sell "+tempQty+" of this stock you'll suffer a loss of Rs"+(-1*tempGain)+" through this transaction.");
					if(stocksBought.get(i).gain+tempGain<0) {
						System.out.println("With this you'll suffer a total loss of Rs"+(-1*(stocksBought.get(i).gain+tempGain))+" for "+stocksBought.get(i).myStk.name);
					}
					else {
						System.out.println("With this your total profit will be reduced to Rs"+stocksBought.get(i).gain+tempGain+" for "+stocksBought.get(i).myStk.name);
					}
				}
				else {
					System.out.println("If you sell this stock you'll gain a profit of Rs"+tempGain+" through this transaction.");
					if(stocksBought.get(i).gain+tempGain<0) {
						System.out.println("With this your total loss will be reduced to Rs"+(-1*(stocksBought.get(i).gain+tempGain))+" for "+stocksBought.get(i).myStk.name);
					}
					else {
						System.out.println("With this you'll gain a total profit of Rs"+stocksBought.get(i).gain+tempGain+" for "+stocksBought.get(i).myStk.name);
					}
				}
				
				System.out.println("Do you really want to sell?(0=no and 1=yes)");
				 int cho =sc.nextInt();
				 if(cho==0) {
					 return;
				 }
				 stocksBought.get(i).soldPrice= stocksBought.get(i).myStk.LTP;
				 stocksBought.get(i).gain=stocksBought.get(i).gain+tempGain;
				 balance+=stocksBought.get(i).myStk.LTP*tempQty;
						 
				 if(stocksBought.get(i).gain<0) {
					 stocksBought.get(i).loss=-1*stocksBought.get(i).gain;
					 stocksBought.get(i).profit=0;
				 }
				 else {
					 stocksBought.get(i).profit=stocksBought.get(i).gain;
					 stocksBought.get(i).loss=0;
				 }
				 
				 if(stocksBought.get(i).qty==tempQty) {
					 
					 float ltp=stocksBought.get(i).myStk.LTP;
					 stk = stocksBought.remove(i);
					 stk.isSold = true;
					 stk.soldPrice=ltp;
					 
				 }
				 else {
					 stocksBought.get(i).qty-=tempQty;
					 stk=new MyStock(stocksBought.get(i).myStk,tempQty);
					 stk.bgtPrice=stocksBought.get(i).bgtPrice;
					 stk.profit=stocksBought.get(i).profit;
					 stk.gain=stocksBought.get(i).gain;
					 stk.loss=stocksBought.get(i).loss;
					 stk.isSold=false;
					 stk.soldPrice=stocksBought.get(i).myStk.LTP;
				 }
				 transactions.add(stk);
				 flag=1;
				 break;
			}
		 }
		 if(flag==0)
		 {
			 System.out.println("No such stock found");
			 
		 }
		 
	 }
}
class UserDatabase{

	File file;								//file
	String name;

	LinkedHashMap<String,User> allUsers=new LinkedHashMap<String,User>();

	transient Scanner sc= new Scanner(System.in);
	transient Scanner scl= new Scanner(System.in);
		
	UserDatabase(String fileName) 							//constructor 
		{
			try {
			file = new File(fileName);						//makes file with the name passed as parameter 
			name=fileName;
			}catch(Exception ex){
				System.out.println("Couldn't make the file");
			}
		}
	
	public void WriteObjectToFile() {
		
        try {
        	FileOutputStream fileOut=new FileOutputStream(name);					//output stream object reference
    		ObjectOutputStream objectOut=new ObjectOutputStream(fileOut);
 
            objectOut.writeObject(allUsers);
            objectOut.close();
 
        } catch (Exception ex) {
        	System.out.println("Error "+ex);
        }
        
	}
	public void ReadObjectFromFile() {
		try {
			
			FileInputStream fileIn = new FileInputStream(name);         // make file input stream obj
			ObjectInputStream objectIn=new ObjectInputStream(fileIn);;
	        
            Object data = objectIn.readObject();
            allUsers=(LinkedHashMap<String,User>)data;
            objectIn.close();
        } catch (Exception ex) {
           
        }
    }
	

	public User register() {                               //method to register if user isn’t already registered
		
		
	    System.out.println();
		String name,password,username;
		Double bal;
		User record;
		while(true) {
			System.out.print("Enter a username of your choice : ");
			username=scl.nextLine();
			if(allUsers.get(username)==null)                     //if username is taken
				break;
			else {
			  System.out.println("Username already exist choose a different one please");
			}
		}

		System.out.print("Enter your name : ");
		name=scl.nextLine();
		System.out.print("Enter your balance : ");
		bal=sc.nextDouble();
		System.out.print("Enter a strong password : ");
		password=sc.next();
		record=new User(username,name,password,bal);
		//System.out.println("Before putting");
		allUsers.put(username,record);
		//System.out.println("After putting");
		System.out.println("Account created successfully");
		return record;
		
	}
	public User login() {                                 //method for already registered customer to login 
		
		User record=null;
		System.out.println();
		String name,password,username;
		System.out.print("Enter your username : ");
		username=scl.nextLine();
		System.out.print("Enter your password");
		password=sc.next();
		record=allUsers.get(username);
		if(record==null) {
			System.out.println("Wrong username!!!");
			return record;
		}
		if((record.password).equals(password)==true) {
			System.out.println("Logged in successfully!");
		}else {
			System.out.println("Invalid password!!!");                 //wrong login credentials
			record=null;
		}
		return record;
	}
	
		public void deleteUser(User removeMe) {
			allUsers.remove(removeMe);
			System.out.println("Account successfully deleted");
		}
	
}
class MyStock implements java.io.Serializable{
	
	Stockdata myStk;
	int qty;
	float bgtPrice;
	float soldPrice;
	float profit;
	float loss;
	float gain;
	boolean isSold=false;
	MyStock(Stockdata myStk,int qty){
		this.myStk=myStk;
		this.qty=qty;
	}
	
}
class Stockdata implements java.io.Serializable{
	String name;
	float LTP;
	float PrevPrice;
	float change_per;
	float change_rs;
	float volume;
	Stockdata(String name, float LTP, float PrevPrice,float change_per, float change_rs,float volume ){
		this.name=name;
		this.LTP=LTP;
		this.PrevPrice=PrevPrice;
		this.change_per=change_per;
		this.change_rs=change_rs;
		this.volume=volume;
	}

}

class Stocks implements java.io.Serializable{
	LinkedHashMap<String,Stockdata> allStocks;
	transient Scanner sc= new Scanner(System.in);
	transient Scanner scl= new Scanner(System.in);
	
	Stocks(){
		
	}
	
public void retrive() {
	Stockdata obj;
	allStocks =new LinkedHashMap<String,Stockdata>(); 
	 final String url = 
                "https://www.financialexpress.com/market/stock-market/nse-top-gainers/";
        
        try {
            final Document document = Jsoup.connect(url).get();
            int i=0;
            for (Element row : document.select(
                "tbody tr")) {
            	
                if ( i<3) {
                	i++;
                    continue;
                }
                else {
                	
                   final String name = 
                           row.select("td:nth-of-type(1)").text();
                   final String LTP  = 
                           row.select("td:nth-of-type(2)").text();
                   final float LTP_f = Float.parseFloat(LTP);
                   final String PrevPrice = 
                           row.select("td:nth-of-type(3)").text();
                   
                   final String PrevPrice1 = 
                		   PrevPrice.replace(",", "");
                    final float PrevPrice1_f = Float.parseFloat(PrevPrice1);
                    
                    final String change_per  = 
	                           row.select("td:nth-of-type(4)").text();
                    final float change_per_f = Float.parseFloat(change_per);
                    
                    final String change_rs  = 
	                           row.select("td:nth-of-type(5)").text();
                    final float change_rs_f = Float.parseFloat(change_rs);
                    
                    final String volume = 
	                           row.select("td:nth-of-type(6)").text();
                    final float volume_f = Float.parseFloat(volume);
                   
                    obj=new Stockdata(name,LTP_f,PrevPrice1_f,change_per_f,change_rs_f,volume_f);
                    allStocks.put(name,obj); 
                    //System.out.println(ticker + " " + name + " " + tempPrice1);
                    
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
}
public Stockdata getStock(String stkname) {
		
	
	Set<Map.Entry<String,Stockdata>> s = allStocks.entrySet();
	 Iterator<Map.Entry<String,Stockdata>> itr =s.iterator();
	 while (itr.hasNext()) { 
	       // Getting Key
		Map.Entry<String,Stockdata> entry = (Map.Entry<String,Stockdata>) itr.next();
	      String name = entry.getKey();
	      Stockdata stock= entry.getValue();
	      if(name.equals(stkname)==true) {
	    	  return stock;
	      }
	 }
	return null;
	}
public void displayStocks() {
	
	Set<Map.Entry<String,Stockdata>> s = allStocks.entrySet();
	 Iterator<Map.Entry<String,Stockdata>> itr =s.iterator();
	 int i=0,j=1,ch=0;
	 System.out.println("*PAGE NUMBER 1*");
	 System.out.printf("%35s %15s %15s %15s %15s %15s \n","Stock Name","LTP", "Previous Price","%Change","Change","Traded Volume");
	 while (itr.hasNext()) { 
	       // Getting Key
		 if(i<100) { 
			 Map.Entry<String,Stockdata> entry = (Map.Entry<String,Stockdata>) itr.next();
			 String str = entry.getKey();
			 Stockdata data= entry.getValue();
			 System.out.printf("%35s %15s %15s %15s %15s %15s \n",str,data.LTP, data.PrevPrice,data.change_per,data.change_rs,data.volume);
			 i++;
		 }
		 else {
			 j++;
			 System.out.println("\nWould you like to move to page number "+j+"?");
			 System.out.print("Enter 1 for yes or 0 for no : ");
			 ch=sc.nextInt();
			 if(ch==0) {
				 break;
			 }
			 else {
				 i=0;
				 System.out.println("\n*PAGE NUMBER "+j+"*");
				 System.out.printf("%35s %15s %15s %15s %15s %15s \n","Stock Name","LTP", "Previous Price","%Change","Change","Traded Volume");
			 }
		 }
	 }
}
public void displayAStock() {
	
	System.out.println("Enter the name of the stock whose current data you want to see : ");
	String nm = scl.nextLine();
	Stockdata stk = getStock(nm);
	System.out.printf("%35s %15s %15s %15s %15s %15s \n","Stock Name","LTP", "Previous Price","%Change","Change","Traded Volume");
	System.out.printf("%35s %15s %15s %15s %15s %15s \n",stk.name,stk.LTP, stk.PrevPrice,stk.change_per,stk.change_rs,stk.volume);

}
}
public class StockManagementProject {
	static  Scanner sc= new Scanner(System.in);
	static Scanner scl= new Scanner(System.in);
	
	public void userMenu(User user,UserDatabase db, Stocks obj) {
		int choice=1;
		while(choice!=13) {
			System.out.println("\n****USER MENU****\n"
					+ "1.My personal details\n"
					+"2.Reset password\n"
					+"3.View stocks\n"
					+"4.View a particular stock\n"
					+"5.Add item to cart\n"
					+"6.View cart\n"
					+ "7.Buy all items from the cart\n"
					+ "8.Buy a particular stock\n"
					+ "9.View bought stocks\n"
					+ "10.Sell stocks\n"
					+ "11.View transaction history\n"
					+ "12.Delete my account\n"
					+ "13.Exit");
			System.out.println("\nEnter your choice: ");
			choice=sc.nextInt();
		switch(choice) {
		case 1: 
			user.display_user();
        	break;
		case 2: 
			user.resetPassword();
			break;
		case 3:
			obj.displayStocks();
			break;
		case 4:
			obj.displayAStock();
			break;
		case 5:
			System.out.println("Enter name of stock you want to add in cart");
			String s= scl.nextLine();
			user.addToCart(s);
			break;
		case 6:
			user.displayCart();
			break;
		case 7:
			user.purchaseCart();
			break;
		case 8:
			System.out.println("Enter the name of the stock you want to buy ");
			String stkname=scl.nextLine();
			user.purchase(stkname);
			break;
		case 9:
			user.displayBoughtstk();
			break;
		case 10:
			user.sell();
			break;
		case 11:
			user.displaytransacs();
			 break;
		case 12:
			db.deleteUser(user);
			break;
		default:
			System.out.println("Enter a valid choice");
				
		}
		}
	}
	
	public static void main(String[] args) {
		
		Stocks obj=new Stocks();
		obj.retrive();
		UserDatabase db=new UserDatabase("Users.txt");
	    db.ReadObjectFromFile();
	    StockManagementProject pr = new StockManagementProject();
	    int choice=0;
	    
	    
	    do {
	    	System.out.println("\n****MENU***");
	    	System.out.println("1)Register\n2)Login\n3)View current market conditions\n4)Exit");
	    	System.out.print("\nEnter your choice : ");
	    	choice = sc.nextInt();
	    	
	    	switch(choice) {
	    	case 1:
	    		User user = db.register();
	    		user.refresh=obj;
	    		pr.userMenu(user,db,obj);
	    		break;
	    	case 2:
	    		User user1 = db.login();
	    		user1.refresh=obj;
	    		pr.userMenu(user1,db,obj);
	    		break;
	    	case 3:
	    		int ch =0;
	    		do {
	    			System.out.println("**MENU***");
	    	    	System.out.println("1)View all stocks\n2)View a particular stock\n3)Exit");
	    	    	System.out.print("\nEnter your choice : ");
	    	    	ch = sc.nextInt();
	    	    	switch(ch) {
	    	    	case 1:
	    	    		obj.displayStocks();
	    	    		break;
	    	    	case 2:
	    	    		obj.displayAStock();
	    	    		break;
	    	    	case 3:
	    	    		System.out.println();
	    	    		break;
	    	    	default:
	    	    		System.out.println("Invalid Input!!!");
	    	    	}
	    		}while(ch!=3);
	    		break;
	    	case 4:
	    		System.out.println("****Thank you******");
	    		break;
	    	default:
	    		System.out.println("Invalid Input!!!");
	    	}
	    }while(choice!=4);

	}
}