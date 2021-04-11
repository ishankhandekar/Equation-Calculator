import java.math.BigDecimal;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


class EquationCalculator() {
    public void run() {
            Scanner scan = new Scanner(System.in);
            String equation = "";
            System.out.println(
                    "Put in any equation and the computer will solve it for you: operation's this supports is '+', '-', '/', '*', '^' and '),},]");
            equation = scan.nextLine();
            scan.close();
            equation = equation.replaceAll(" ", "");
            equation = equation.replaceAll("\\[", "\\(");
            equation = equation.replaceAll("\\]", "\\)");
            equation = equation.replaceAll("\\{", "\\(");
            equation = equation.replaceAll("\\}", "\\)");

            System.out.println("Parsing equation.");

            equation = putSpaces(equation);
            System.out.println("Equation is now '" + equation + "'");
            while (equation.contains("(")) {
                equation = solvesLastparentheses(equation);
            }

            System.out.println("Answer is " + solveExpression(equation));
        }

        public static boolean isIncorrectEquation(String equation) {
            for (int i = 0; i < equation.length(); i++) {
                if (Character.isDigit(equation.charAt(i)) == false && equation.charAt(i) != '.' && equation.charAt(i) != '-'
                        && equation.charAt(i) != '(' && equation.charAt(i) != ')' && equation.charAt(i) != '+'
                        && equation.charAt(i) != '*' && equation.charAt(i) != '/' && equation.charAt(i) != '^'
                        && equation.charAt(i) != 'E') {
                    System.err.println("Some of these characters are wrong. Type another equation");
                    return true;
                }
            }
            if (equation.length() - equation.replaceAll("(", "").length() != equation.length()
                    - equation.replaceAll(")", "").length()) {
                System.err.println(
                        "This equation has a parentheses that doesn't match up with another one. Type another  equation");
                return true;
            }

            return false;

        }

        public String putSpaces(String equation) {
            boolean isOperation = true;
            int i = 0;

            while (i < equation.length()) {
                Character c = equation.charAt(i);
                if ((c.isDigit(equation.charAt(i)) == true || equation.charAt(i) == '-') && (isOperation == true)) {
                    int a = endOfNumber(i, equation);

                    if (a == -1) {
                        equation += ' ';
                        return equation;
                    }
                    equation = addChar(equation, ' ', a + 1);
                    i = a + 2;
                    isOperation = false;
                    continue;
                }

                if ((c == '-' || c == '^' || c == '/' || c == '*' || c == '+') && (isOperation == false)) {
                    equation = addChar(equation, ' ', i + 1);
                    i = i + 2;
                    isOperation = true;
                    continue;
                }

                if (c == '(' || c == ')') {
                    equation = addChar(equation, ' ', i + 1);
                    i += 2;
                    continue;
                }
                i++;
            }

            return equation;
        }

        public String solvesLastparentheses(String toSolve) {
            int openParenthesesIndex = -1;
            int closingParenthesesIndex = -1;
            for (int i = toSolve.length() - 1; i >= 0; i--) {
                if (toSolve.charAt(i) == '(') {
                    openParenthesesIndex = i;
                    break;
                }
            }
            if (openParenthesesIndex == -1) {
                return "Nothing";
            }
            for (int i = openParenthesesIndex + 1; i < toSolve.length(); i++) {
                if (toSolve.charAt(i) == ')') {
                    closingParenthesesIndex = i;
                    break;
                }
            }
            assert (closingParenthesesIndex >= -1) : "Mismatched parentheses";
            String expression = toSolve.substring(openParenthesesIndex + 1, closingParenthesesIndex);
            System.out.println("Now solving " + expression);
            double answer = solveExpression(expression);
            toSolve = replace(openParenthesesIndex, closingParenthesesIndex, toSolve, answer);
            System.out.println("New equation is " + toSolve);
            return toSolve;
        }

        // solves expression without parentheses

        public double solveExpression(String expression) {
        
            int indexOfOperator = -1;
            for (int i = 0; i < 3; i++) {
                String operator1 = "";
                String operator2 = "";
                if (i == 0) {
                    operator1 = " ^ ";
                    operator2 = " ^ ";
                } else if (i == 1) {
                    operator2 = " / ";
                    operator1 = " * ";
                } else if (i == 2) {
                    operator1 = " + ";
                    operator2 = " - ";
                }

                while (expression.indexOf(operator1) != -1 || expression.indexOf(operator2) != -1) {

                    int Index = -1;
                    if (expression.indexOf(operator1) == -1) {
                        Index = expression.indexOf(operator2) + 1;
                    } else if (expression.indexOf(operator2) == -1) {
                        Index = expression.indexOf(operator1) + 1;
                    } else {
                        Index = expression.indexOf(operator1) < expression.indexOf(operator2)
                                ? expression.indexOf(operator1) + 1
                                : expression.indexOf(operator2) + 1; // all the +1's are needed because operator1 and 2
                                                                    // begin with a space
                    }

                    if (Index <= 0) {
                        System.err.println("Index has to be bigger than 0 at this point");
                        System.exit(-1);
                    }

                    int index1 = Index - 2;

                    for (int j = Index - 2; j >= 0; j--) {
                        if (expression.charAt(j) == ' ') {
                            index1 = j + 1;
                            break;
                        }
                        index1 = 0;
                    }

                    int index2 = Index + 2;
                    for (int j = Index + 2; j < expression.length(); j++) {

                        if (expression.charAt(j) == ' ') {
                            index2 = j - 1;
                            break;
                        }
                        index2 = expression.length() - 1;
                    }

                    
                    System.out.println("        Now solving " + expression.substring(index1, index2 + 1));

                    double answer = solveOperation(expression.substring(index1, index2 + 1));

                    expression = replace(index1, index2, expression, answer);
                    System.out.println("    New equation is " + expression);

                }
            }

            return Double.parseDouble(expression);

        }

        int countCharInString(String str, char letter) {
            int counter = 0;
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == letter) {
                    counter++;
                }
            }
            return counter;
        }

        String replace(int index1, int index2, String expression, double answer) {
            String expression2 = "";
            for (int i = 0; i < expression.length(); i++) {
                if (i == index1) {
                    expression2 += answer;
                    i = index2;
                } else {
                    expression2 += expression.charAt(i);
                }
            }
            return expression2;
        }

        double solveOperation(String toSolve) {
            toSolve += ' ';
            String firstNumber = "";
            String secondNumber = "";
            int firstNumberIndex = -1;
            char operator = ' ';
            int operatorIndex = -1;
            for (int i = 0; i < toSolve.length(); i++) {

                if (toSolve.charAt(i) != ' ') {
                    firstNumber += toSolve.charAt(i);

                } else {
                    firstNumberIndex = i - 1;
                    break;
                }
            }


            double firstNumber2 = Double.parseDouble(firstNumber);

            operator = toSolve.charAt(firstNumberIndex + 2);
            
                if (toSolve.charAt(i) != ' ') {
                    secondNumber += toSolve.charAt(i);
                } else {
                    break;
                }
            }
        

            double secondNumber2 = Double.parseDouble(secondNumber);

            if (operator == '+') {
                return firstNumber2 + secondNumber2;
            } else if (operator == '-') {
                return firstNumber2 - secondNumber2;

            } else if (operator == '*') {
                return firstNumber2 * secondNumber2;
            } else if (operator == '/') {

                if (secondNumber2 == 0) {
                    System.err.println(toSolve + " is UNDEFINED");
                    System.exit(-1);
                } else {

                    return firstNumber2 / secondNumber2;
                }

            } else if (operator == '^') {
                return Math.pow(firstNumber2, secondNumber2);
            }
            return 0;

        }

        public String addChar(String str, char ch, int position) {
            return str.substring(0, position) + ch + str.substring(position);
        }

        public int endOfNumber(int startIndex, String equation) {
            int endIndex = startIndex;
            Character e = equation.charAt(startIndex);
            if (equation.charAt(startIndex) != '.' && equation.charAt(startIndex) != '-'
                    && e.isDigit(equation.charAt(startIndex)) == false) {
                System.err.println("error");
                System.exit(0);
            }

            for (int i = startIndex; i < equation.length(); i++) {
                Character c = equation.charAt(i);
                if (i == startIndex && equation.charAt(i) == '-') {
                    endIndex++;
                    continue;
                }
                if (c.isDigit(equation.charAt(i)) || equation.charAt(i) == '.') {
                    endIndex += 1;
                    continue;
                }
                return endIndex - 1;
            }
            return -1;
        }
    }
}