This is a version of OSSPRMapper4 made to handle the rmca project data. The main difference is it is changed to handle pr numbers as strings instead of ints.

# OSSPRMapper4
OSSPRMApper4 address the load of author skills and the creation of csv to process author predictions
### How to Run

#### Args example
![image](https://user-images.githubusercontent.com/59481467/128212226-c3724885-a0dd-41e7-8779-b7d961c9bd02.png)



#### Relevant source code

		user        = args[0];
		pswd        = args[1];
		project     = args[2];
		db          = args[3];
		file        = args[4];
		csv         = args[5];
		isOnlyCSV   = Integer.parseInt(args[6]);
		separator   = args[7];
		bin         = args[8];
		classes     = args[9];
		if (isOnlyCSV==1) {
			getPrs(); // apriori body title
			genBinaryExit(); //binary body title
		}
		else {
			readData();
		}
	}

Updated arguments example:

postgres

123

audacity

audacity_cpp

filesPR3BodyTitle2

aprioriBodyTitle.csv

1

;

binaryBodyTitle.csv

PRClasses.txt

/Users/fd252/OneDrive/Production/ETL1-Pipeline-main/data/outputs/new/
