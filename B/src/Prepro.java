import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;	
import java.util.ArrayList;

public class Prepro 
{
	static double DampingFactor = 0.85;
	static int numOfNodes = 875713;

//This program wants to transform the original dataset into table only with non-zero matrix just like the one in the powerpoint lecture notes.
//Result will be in format of <Source node> , <Degree> , <Destination nodes>. To make the size of the data set smaller.
//This program also initiate the entries of PageRank to (1-DampingFactor)/N	
		public static void main(String[] args) throws FileNotFoundException, IOException
		{
			prepro();
			initiate();
		}
		
		public static void initiate() throws IOException
		{
			//Just a writer.
			PrintWriter pw = new PrintWriter(new File("src/r0.txt"));
			pw.println("Node Id, PageRank Scores");
			
			try(BufferedReader br = new BufferedReader(new FileReader("src/pre-processed.txt")))
			{
				//Skipping first Line. It is just description of the format.
				br.readLine();

				//Each line will be <NodeID>, (1-Damping factor) / number of nodes.
				for(String line ; (line=br.readLine()) != null;)
				{
					String[] temp = line.split(",");
					pw.println(temp[0] + "," + (1-DampingFactor)/numOfNodes);
				}
			}
			pw.flush();
			pw.close();
		}
		
		public static void prepro() throws IOException
		{
			//Just a writer that's all.
			PrintWriter pw = new PrintWriter(new File("src/pre-processed.txt"));
			pw.println("Source Node, Degree , [Destination nodes]");
			//First input in from node 0. This string keep track of which node is being processed.
			String current = "0";
			//This is used for keeping track of the intermediate result nodes.
			ArrayList<String> result = new ArrayList<String>();
			//Degree of current checking nodes.
			int degree = 0;
			

			try(BufferedReader br = new BufferedReader(new FileReader("src/web-Google.txt")))
			{
				//These 4 lines is just here to skip the first 4 lines of the data as it is useless in this program.
				br.readLine();
				br.readLine();
				br.readLine();
				br.readLine();
				
				//This for loop will loop through each line of the text starting with the forth line.
				for(String line; (line = br.readLine())!= null;)
				{
					String[] temp = line.split("\t");
					if(current.equals(temp[0]))
					{
						//If the FromNodeID is the same as the current, Simply increase degree by 1 and add the ToNodeID to an arrayList
						result.add(temp[1]);
						degree++;
					}
					else if(current!= temp[0])
					{
						//If we hit a line where FromNodeID is not the same, We print out the the result for node = current
						pw.println(current + "," + degree + "," + result.toString().replaceAll("[^0-9,]+",""));
						//Then, we move on to the next FromNodeID and increase the degree by one since This line already have a ToNodeID
						//Clear the arrayList and add the ToNodeID to new result.
						current = temp[0];
						degree = 1;
						result.clear();
						result.add(temp[1]);
					}
					
				}
				//Printing out the very last line.
				pw.println(current + "," + degree + "," + result.toString().replaceAll("[^0-9,]+",""));
				System.out.println("Done");
			}
			pw.flush();
			pw.close();

		}

		
}
