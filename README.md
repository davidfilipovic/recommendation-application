## Recommendation application for video games on PC, built in Clojure. 

Tehnique used in this application is called collaborative filtering.
Advantage of this technique is that the system does not rely on the values for the feature variables of its items,
and consequently such a system does not need to know about the characteristics of
the items that are provided by it.

One note: in application are included *all* types of games that are implemented for playing on PC.

If, in any case, you receive a "Slow down, 429 Too Many Requests" error, just refresh the page again. This is because Metacritics have some kind of restricting policy related to crawling pages. 

## Instaling instructions

Running this application is fairly simple. First you'll need to download it, in either way by cloning this repo to desktop or by downloading zip file.

Then, you will need a databeas. To start it, get Mongo Database from this [link](http://www.mongodb.org), install it, then navigate to **../bin/mongodb.exe** and wait until it has made connection to localhost. 

After that, you'll need to download and install [Leiningen](http://leiningen.org). If your machine is running on Microsoft Windows, than you can handy get [win instaler](http://leiningen-win-installer.djpowell.net), which will do all dirty work width PATH variable and similar stuff for you. Note that if you have CounterClockwise instaled as a Eclipse plugin, then you already have Leiningen, and all you have to do to start application is to open project in Eclipse (Import->General->Existing Projects into Workaspace), press Alt L, L (or right click on project, and then Leiningen->Generic Leiningen Command Line) and type **run**. 
If you don't have Eclipse, after you have installed Leiningen open the command prompt, navigate to project folder and type **lein run**.

## Libraries used 



## Literature

[Practical Clojure](http://www.amazon.com/Practical-Clojure-Experts-Voice-Source-ebook/dp/B003VM7G3S)

This is the first book I have read about a Clojure. It is a good introductory book, and can give you good insight about functional programming, as well as basic Clojure functions and some features. Here you can learn how to set up Clojure environment for some basic real-world task. 

[Programming Clojure, Second Edition](http://www.amazon.com/Programming-Clojure-Stuart-Halloway/dp/1934356867)

Second edition of this book presents some changes in Clojure, since it was first introduced in 2007. It has some good explanation about protocols, multimethods, and there is a whole chapter about Clojure's connection to Java.

[Clojure Programming](http://www.amazon.com/Clojure-Programming-Chas-Emerick/dp/1449394701/ref=pd_sim_b_1?ie=UTF8&refRID=0KCSHHVCSA3Z3YCX6JAF)

Clojure programming is definetely the most comprehensive book about a toopic. As Practical Clojure and Programming Clojure it contains explanation about functional languages in general, as well as explanations of Clojure functions and other features, but it goes much deeper into the core of the languge, explaining all pros of working with CLojure. Although it is a excellent reference, I would strongly recommend to first read the two first books (and maybe [The Joy of Clojure](http://www.amazon.com/The-Joy-Clojure-Thinking-Way/dp/1935182641/ref=pd_sim_b_2?ie=UTF8&refRID=0KCSHHVCSA3Z3YCX6JAF)) before you start reading this book.   

[Web Development with Clojure](http://www.amazon.com/Web-Development-Clojure-Build-Bulletproof/dp/1937785645/ref=pd_sim_b_3?ie=UTF8&refRID=0KCSHHVCSA3Z3YCX6JAF)

As its title says, this book is about developing a web applications using Clojure. It is written in a good, easy to understand manner, and for me it has excellent informations about vastly of available libraries, tools and good practices out there, that can be used in bulding your application. This is definetely a must-read book about web development in CLojure, and I have used it a lot during bulding my application. Also, this book is published in 2014, so is definetely up to date, which is very important in programming books. 

[Clojure for Machine Learning](http://www.amazon.com/Clojure-Machine-Learning-Akhil-Wali/dp/1783284358)

As Web Development in Clojure, this book have also appeared in 2014, and covers a plenty of techniques and algorithms for machine learning and their implementations in Clojure. Among other things, in this book are described techniques for building neural networks, understanding linear regression, categorizing and clustering data, working with matrices, etc. Of course, in seperate chapter there is explanation of recommendation system, which is divided into two sections, first about content-based filtering second about collaborative filtering.    




