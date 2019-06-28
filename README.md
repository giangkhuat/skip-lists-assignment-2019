# skip-lists-assignment-2019
Code for the 2019 Skip Lists assignment


Author: Giang Khuat and Henry Firestone

Class CSc207-02 Spring 2019

This is a class assignment for CSC207 Computer Science class Spring 2019. In this assignment, we implement skip lists, a kind of data structure.

Skip lists solve the problem in another way, by having multiple forward links from each node, with the links at level 
0 stepping through every element, the links at level 1 skipping about 1/2 the elements, the links at level 2 skipping about 3/4 the elements, and so on and so forth. Skip lists take advantage of a random number generator to help ensure that we get the appropriate distribution of values. Most of the time, skip lists require O(log_n_) steps to find an element, O(log_n_) steps to insert an element, and O(log_n_) steps to remove an element.


Refereces:
  
        * Get help from Oen, evening tutor ; Sam Rebelsky for part 3
        
        * Get help from Rob Lorch and Gabby
        
        * William Pugh. 1990. Skip lists: a probabilistic alternative to balanced trees. Commun. ACM 33, 6 (June 1990), 668-676. DOI=10.1145/78973.78977 http://doi.acm.org/10.1145/78973.78977.
