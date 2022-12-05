

Compilation Instructions
------------------------

1. Install maven

2. Compile using maven

`mvn package`

3. Execute

`java -jar target/compiler-0.0.3.jar <input-filename>`

or

`java -cp target/compiler-0.0.3.jar Compiler <input-filename>`


Update 1.0
------------------------

<br>

### Compiler functionality<br><br>

* Compiler now supports string variables and functions
    * Strings can be declared and assigned as follows:<br><br>
    
    ```
    string name;
    name = "John Doe";
    ```
    <br>

    * String functions can be defined as all other types:<br><br>
    ```
    string hellostring(){
        return "hello world!!";
    }
    ```
    <br>
* Removed unnecessary **struct** keyword when declaring structs or struct arrays:<br><br>
    ```
    struct Person{
        int age;
        string name;
    };

    void main(){
        Person p; //struct Person p;
        return;
    }
    ```
    <br><br>


Assignment 07 Info
------------------------

### Compiler functionality

* Supports struct arrays and nested structs
  * The compiler creates a Program.class file for the main program, and one .class file
  for every struct that is defined, under the root directory of the project
  
* Operator ! (NOT) is not yet implemented

* Assigning an int to a float will throw an error 

ex.
```
float a;
a = 10; /*Will throw an "Expecting to find float on stack" error*/
----------
a = 10.0; /*Compiles*/
```

* In assignment statements the left side expression can only be one of the following types:
    
    * `IdentifierExpression`
    * `IdentifierBracketExpression` (array)
    * `ExprDotIdentifier` or `ExpressionDotArrayElementExpression` (struct fields)

* Only functions can be of type `VOID`. Any variable defined as VOID will throw an error

### Example Files - Test The Compiler

* Under the .\examples\ directory there are 4 valid test files you can try
  * valid1.txt
  * valid2.txt
  * valid3.txt
  * valid4.txt
* And one invalid that will not compile
  * invalid1.txt


Assignment 06 Info
------------------------

* Various precedence order changes



Assignment 05 Info
------------------------

### Extra Compiler Functionality

* Functions
    
    * All code paths of a function should return a value, else an error is raised (**Not all code paths return a value**)
    ex.
    ```
    bool check(float a){
        if(a < 0) a = a + 1;
        else return true;
        
        /* error: not all codepaths return a value */
    }  
  
    ```
    
    * Code below a return statement will raise an **Unreachable statement** error
    
    * The return value of a funtion must be of a less or equal type to the funtion's return type. This
    also applies to nested return statements
    
    ex.
    ```
    int add(int a, float b){
        if(a < b){
            if(a < 0) print(a);
            else{
                if(b > 100) return 0.45; //error: wrong return type
            }    
        }
    }  
  
    ```
    
* Structs
    * Refering to a non existing struct value will raise a **Struct does not contain this variable** error
      
      ex.
      ```
      struct complex{
          int real;
          int img;
      }
      
      void main(){
          struct complex c;
          c.real = 10; //compiles
          c.x = 4; // error: struct c does not contain a variable called x
      }
      ```
* Assumptions
    * The expression: *Expr . Identifier* can only be used with structs to access a variable, else it throws an error
    
* Fixes
    * Dot operator now has higher priority than mathematical and logical operators 

Assingment 04 Info
------------------------

- Added struct type functions

- Added symbol tables in nodes

- Added "-expect 1" program argument for shifting in if else statements


Assignment 03 Info
------------------------

* The compilation starts with the root element which is a Program

* A Program consists of none to many CompilationUnits.

* A CompilationUnit consists of one of the following:

    * <VariableDefinitionStatement\>*
    * <FunctionDefinitionStatement\>*
    * <StructDeclarationStatement\>*


