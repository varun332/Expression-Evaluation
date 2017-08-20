package apps;

import java.io.*;
import java.util.*;
import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;

    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
            /** COMPLETE THIS METHOD **/
        ScalarSymbol sc;
        ArraySymbol ar;
        
        char[] giArray = expr.toCharArray();
        arrays = new ArrayList<ArraySymbol>();
        scalars = new ArrayList<ScalarSymbol>();
        String preSymbol = "";
        
        for (int i = 0; i < giArray.length; i++) {
            if (!delims.contains("" + giArray[i])) {
                if (!Character.isDigit(giArray[i])) {
                    preSymbol = preSymbol + giArray[i];
                }
            }
            
            else if (delims.contains("" + giArray[i])) {
                if (preSymbol.length() < 1) {
                    continue;
                }
                
                else {
                    if (giArray[i] == '[' && preSymbol.length() > 0) {
                        ar = new ArraySymbol(preSymbol);
                        if (!arrays.contains(ar)) {
                            arrays.add(ar);
                        }
                        
                       preSymbol = "";
                    }
                    
                    else if (preSymbol.length() > 0) {
                        sc = new ScalarSymbol(preSymbol);
                        if (!scalars.contains(sc)) {
                            scalars.add(sc);
                        }
                        
                        preSymbol = "";
                    }
                }
            }
        }
        
        sc = new ScalarSymbol (preSymbol);
        if (!scalars.contains(sc)) {
            scalars.add(sc);
        }
        
        preSymbol = "";
        return;
    }
    
    private boolean emp (String newExpress) {
		char[] charArr = newExpress.toCharArray();
		
		for(char c: charArr) {
			if(((c <= 'z') && (c >= 'a')) || ((c <= 'Z') && (c >= 'A'))) {
				return false;
			}
		}
		
		return true;
	}

	private String bracketIndex (ArrayList<Integer> in, ArrayList<Integer> out, String newExpress) {
    	in.clear();
        out.clear();
    	Stack<Integer> opBrack = new Stack<Integer>();
    	char[] giArray = newExpress.toCharArray();
    	for (int pos = 0; pos <= giArray.length-1; pos++ ) {
	    	
    		if((giArray[pos]=='[')) {
	    		opBrack.push((Integer)pos);
	    	}
	    	
	    	else if((giArray[pos]==']')) {
	    			in.add(opBrack.pop());
        			out.add(pos);
        	}
    	}
    	return "";
    }
	
    private String bracketName (Stack<String> brackEva, String newExpress) {
    	StringTokenizer str = new StringTokenizer(newExpress, delims, true);
    	String token = "";
        
        while (str.hasMoreTokens()) {
	        token = str.nextToken();
	        
	        if ((token.charAt(0) <= 'z' && token.charAt(0) >= 'a') || (token.charAt(0) <= 'Z' && token.charAt(0) >= 'A' || token.equals("["))) {
	        	brackEva.push(token);	        
	        }
        }
        
        while(!brackEva.isEmpty()) {
	        token = brackEva.pop();
	        
	        if (token.equals("[")) {
	            token = brackEva.pop();
	            return token;
	        }
        }
        
        return token;
    }
    
    private String evalBrack (String newExpress) {
    	Stack<String> brackEva = new Stack<String>();
    	ArrayList<Integer> in = new ArrayList<Integer>();
        ArrayList<Integer> out = new ArrayList<Integer>();
    	bracketIndex(in, out, newExpress);
    	String breakStr = "";
    	String endArr = bracketName(brackEva, newExpress);
    	while(!in.isEmpty()) {
    		if(emp(newExpress) == true) {
    			break;
    		}
    		
    		bracketIndex(in, out, newExpress);
    		breakStr = newExpress.substring(in.get(0)+1, out.get(0));
    		
    		int eval = (int) numEva(breakStr);
    		newExpress = newExpress.replace(breakStr, Integer.toString((int) numEva(breakStr)));
    		ArraySymbol arrSym = new ArraySymbol(endArr);
    		int count;

    		if(eval > arrays.get(arrays.indexOf(arrSym)).values.length) {
    			count = 0;
    		}
    		else {
    			count = arrays.get(arrays.indexOf(arrSym)).values[eval];
    		}
    		
    		String toReplace = endArr+"["+eval+"]";
    		newExpress = newExpress.replace(toReplace, Integer.toString(count) );
    		
    		if(brackEva.isEmpty()) {
    			continue;
    		}
    		
    		else {
    			brackEva.pop();
    			endArr = brackEva.pop();
    		}
    		
    		in.remove(0);
    		out.remove(0);
    	}
    	
    	return newExpress;
    }
    
    private static Boolean checkOp (String let){
		switch(let) {
			case "(":
			case ")":
			case "*":
			case "/":
			case "+":
			case "-":
				return true;
			default: 
				return false;
		}
	}
    
    private static Float operate (Float tw1, Float tw2, String let) {
    	switch(let) {
		case "*":
			return (tw1 * tw2);
		case "/":
			return (tw1 / tw2);
		case "%":
			return (tw1 % tw2);
		case "+":
			return (tw1 + tw2);
		case "-":	
			return (tw1 - tw2);
		}
    	
		return null;		
	}		
    
    private static void calculator (Stack<String> numStack, Stack<String> addiviStack) {
		float number2 = Float.parseFloat(numStack.pop());
		float number1 = Float.parseFloat(numStack.pop());
		String operator = addiviStack.pop();
		
		float answer;
		answer = operate(number1, number2, operator);
		numStack.push(Float.toString(answer));				
	}
	
    private static int priority (String let) {
		switch(let) {
		case "(":
		case ")":
			return 3;
		case "*":
		case "/":
			return 2;
		case "+":
		case "-":
			return 1;
		default :
			return -1;
		}
	}
    
    private static float numEva (String newExpress) {
		newExpress = newExpress.replaceAll("\\s","");
    	StringTokenizer str = new StringTokenizer(newExpress," ()*/+-",true);
		String token = "";
		Stack<String> numStack = new Stack<String>();
		Stack<String> addMultiplyStack = new Stack<String>();
		
		while (str.hasMoreTokens()) {
			token = str.nextToken();
			if (!checkOp(token)) {
				float numb = Float.parseFloat(token);
				addMultiplyStack.push(Float.toString(numb));
			}
			
			else {
				if (numStack.isEmpty()) {
					numStack.push(token);
				}
				
				else if (token.equals("(") || numStack.peek().equals("(")) {
					numStack.push(token);
				}
				
				else if (token.equals(")")) {
					while(!numStack.peek().equals("(")){
						calculator(addMultiplyStack, numStack);
					}
					numStack.pop();
				}
				
				else {
					while (!numStack.isEmpty() && !numStack.peek().equals("(") && priority(token) <= priority(numStack.peek())) {
						calculator(addMultiplyStack, numStack);
					}
					numStack.push(token);
				}				
			}
		}
		
		while(!numStack.isEmpty()){
			calculator(addMultiplyStack, numStack);
		}
		
		return Float.parseFloat(addMultiplyStack.pop());
	}
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    		/** COMPLETE THIS METHOD **/
			// following line just a placeholder for compilation
    	String newExpress = expr;
    	StringTokenizer str = new StringTokenizer(expr, delims, true);
    	String token = "";
    	   	
    	
        while (str.hasMoreTokens()) {
        	token = str.nextToken();
        	
	        if (token.equals("(")) {	
	        	String exCutti = expr.substring(expr.indexOf("(")+1);
	        	newExpress = evaluate(exCutti, newExpress);
	        }
	        
        	else {
        		ScalarSymbol scaSym = new ScalarSymbol(token);
                ArraySymbol arrSym = new ArraySymbol(token);
        		if (arrays.contains(arrSym)) {
        			String exCutti = expr.substring(expr.indexOf("[")+1);
    	        	newExpress = evaluate(exCutti, newExpress);
        		}
        		
        		else if (scalars.contains(scaSym)) {
        			newExpress = newExpress.replaceFirst("\\b"+token+"\\b", Integer.toString(scalars.get(scalars.indexOf(scaSym)).value));
        		}
        	}   	        
        }
        
    	if (newExpress.contains("[")) {
    		newExpress = evalBrack(newExpress);
    	}
    	
    	float answer = numEva(newExpress);
    	return answer;
    }
    
    private String evaluate (String exCutti, String newExpress) {
    	StringTokenizer str = new StringTokenizer(exCutti, delims, true);
    	String token = "";
    	while (str.hasMoreTokens()) {
    		token = str.nextToken();
	    	
    		if (token.equals("(")) {
	        	newExpress = evaluate(exCutti.substring(exCutti.indexOf("(")+1), newExpress);
	        }
	    	
	    	else {
	    		ScalarSymbol scaSym = new ScalarSymbol(token);
                ArraySymbol arrSym = new ArraySymbol(token);
        		if (arrays.contains(arrSym)) {
        			exCutti = exCutti.substring(exCutti.indexOf("[")+1);
    	        	newExpress = evaluate(exCutti, newExpress);
        		}
        		
        		else if (scalars.contains(scaSym)) {
        			newExpress = newExpress.replaceFirst("\\b"+token+"\\b", Integer.toString(scalars.get(scalars.indexOf(scaSym)).value));
        		}
	    	}	
        }
    	
    	return newExpress;
    }

    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    	for (ArraySymbol as: arrays) {
    		System.out.println(as);
    	}
    }

}
