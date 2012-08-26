package ua.com.fielden.platform.expression;

import java.util.ArrayList;
import java.util.List;

import ua.com.fielden.platform.expression.ast.AstNode;
import ua.com.fielden.platform.expression.exception.MismatchedTokenException;
import ua.com.fielden.platform.expression.exception.MissingTokenException;
import ua.com.fielden.platform.expression.exception.NoViableAltException;
import ua.com.fielden.platform.expression.exception.RecognitionException;
import ua.com.fielden.platform.expression.exception.ReservedNameException;
import ua.com.fielden.platform.expression.exception.UnwantedTokenException;

/**
 * The expression language grammar parser, which constructs an AST as it parses sentences in Expression Language.
 * <p>
 * Here is a short-hand grammar rules for Expression Language, where only parsing rules are outlined, omitting lexer rules for brevity.
 * <pre>
 * sentence : logical-expression | arithmetic-expression
 * logical-expression : '(' logical-expression ')' | comparison-expression (logical-op comparison-expression)*
 * comparison-expression : '(' comparison-expression ')' | arithmetic-expression cmp-op arithmetic-expression
 * arithmetic-expression : term (arithmetic-op term)*
 * </pre>
 * 
 * <p>
 * The main method is {@link #parse()} that either returns a root node of the constructed AST or throws {@link RecognitionException}.
 * <p>
 * Here is a usage example:
 * 
 * <pre>
 * final ExpressionLexer el = new ExpressionLexer(&quot;9 + (6 + 2) * 6 - (SUM (property.subPropety)+3) / (34 - prop)&quot;);
 * 
 * final ExpressionParser parser = new ExpressionParser(el.tokenize());
 * final AstNode root = parser.parse();
 * System.out.println(root.treeToString());
 * </pre>
 * 
 * @author TG Team
 * 
 */
public class ExpressionParser {
    private final Token[] tokens;
    private int position;
    private final List<Integer> markers = new ArrayList<>();

    /**
     * Construct an array of tokens to be parsed.
     * 
     * @param tokens
     */
    public ExpressionParser(final Token[] tokens) {
	this.tokens = tokens;
	position = 0;
    }

    /**
     * The main API method to be used for triggering input parsing. Should be invoked only once.
     * 
     * @return
     * @throws RecognitionException
     */
    public AstNode parse() throws RecognitionException {
	final AstNode root = sentence();
	if (position < tokens.length) {
	    throw new UnwantedTokenException("Unwanted token " + tokens[position], tokens[position]);
	}
	return root;
    }

    /**
     * Implements parsing of the rule sentence: <code><b>sentence ::= logical-expression | arithmetic_expression</b></code>
     * It uses the backtracking pattern with arbitrary lookahead to properly identify the matching rule.
     * 
     * @return
     * @throws RecognitionException
     */
    private AstNode sentence() throws RecognitionException {
	if (speculate_logical_expression()) {
	    return match_logical_expression();
	} else {
	    return match_arithmetic_expression();
	}
    }

    private boolean speculate_logical_expression() {
	mark();
	try {
	    match_logical_expression();
	    return true;
	} catch (final Exception ex) {
	    return false;
	} finally {
	    release();
	}
    }

    private boolean speculate_cmp_expression_with_paren() {
	mark();
	try {
	    match_cmp_expression_with_paren();
	    return true;
	} catch (final Exception ex) {
	    return false;
	} finally {
	    release();
	}
    }

    /**
     * Logical expressions may have several comparison expression chained by arbitrary number of logical operations || and &&, grouped by parentheses as deemed necessary.
     * Both logical operations are considered of the same precedence. Parentheses should be used to impose any specific order.
     * 
     * @return
     * @throws RecognitionException
     */
    private AstNode match_logical_expression() throws RecognitionException {
	if (lookahead() == EgTokenCategory.LPAREN) {
	    // the LPAREN ahead may or may not be part of the logical expression.
	    // e.g. it could be part of the comparison expression used as part of the logical expression
	    // thus, it is necessary to speculate and if succefull backtrac and match logical expression in parentheses
	    if (speculate_logical_expression_with_paren()) {
		return match_logical_expression_with_paren();
	    } else {
		final AstNode leftNode = match_cmp_expression();
		if (lookahead() == EgTokenCategory.AND || lookahead() == EgTokenCategory.OR) {
		    final AstNode node = logical_op();
		    final AstNode rightNode = match_logical_expression();
		    return node.addChild(leftNode).addChild(rightNode);
		}
		return leftNode;
	    }
	} else {
	    final AstNode leftNode = match_cmp_expression();
	    if (lookahead() == EgTokenCategory.AND || lookahead() == EgTokenCategory.OR) {
		final AstNode node = logical_op();
		final AstNode rightNode = match_logical_expression();
		return node.addChild(leftNode).addChild(rightNode);
	    }
	    return leftNode;
	}
    }

    /**
     * This matching is used as part of the logical expression matching where it is suspected that expression is surounded with parentheses.
     * 
     * @return
     * @throws RecognitionException
     */
    private AstNode match_logical_expression_with_paren() throws RecognitionException {
	match(EgTokenCategory.LPAREN);
	final AstNode node = match_logical_expression();
	if (node.getToken().category != EgTokenCategory.AND && node.getToken().category != EgTokenCategory.OR) {
	    throw new NoViableAltException("Could not complete parsing of a logical expression with parentheses.", tokens[position]);
	}
	match(EgTokenCategory.RPAREN);
	// logical expression surounded in parentheses may be followed by another logical operation, which needs to be checked
	if (lookahead() == EgTokenCategory.AND || lookahead() == EgTokenCategory.OR) {
	    final AstNode  nextNode = logical_op();
	    final AstNode rightNode = match_logical_expression();
	    return nextNode.addChild(node).addChild(rightNode);
	}
	return node;
    }

    private boolean speculate_logical_expression_with_paren() {
	mark();
	try {
	    match_logical_expression_with_paren();
	    return true;
	} catch (final Exception ex) {
	    return false;
	} finally {
	    release();
	}
    }

    private AstNode match_cmp_expression() throws RecognitionException {
	if (lookahead() == EgTokenCategory.LPAREN) {
	    if (speculate_cmp_expression_with_paren()) {
		return match_cmp_expression_with_paren();
	    } else {
		final AstNode leftNode = match_arithmetic_expression();
		final AstNode node = cmp_op();
		final AstNode rightNode = match_arithmetic_expression();
		return node.addChild(leftNode).addChild(rightNode);
	    }
	} else {
	    final AstNode leftNode = match_arithmetic_expression();
	    final AstNode node = cmp_op();
	    final AstNode rightNode = match_arithmetic_expression();
	    return node.addChild(leftNode).addChild(rightNode);
	}
    }

    private AstNode match_cmp_expression_with_paren() throws RecognitionException {
	match(EgTokenCategory.LPAREN);
	final AstNode node = match_cmp_expression();
	match(EgTokenCategory.RPAREN);
	return node;
    }

    private int mark() {
	markers.add(position);
	return position;
    }

    private void release() {
	final int marker = markers.get(markers.size() - 1);
	markers.remove(markers.size() - 1);
	seek(marker);
    }

    private void seek(final int marker) {
	position = marker;
    }

    /**
     * Produces an AT node representing an arithmetic expression not surounded by parentheses.
     * 
     * @return
     * @throws RecognitionException
     */
    private AstNode match_arithmetic_expression() throws RecognitionException {
	final AstNode leftNode = term();
	return arithmetic_operation(leftNode);
    }

    /**
     * Produces an AST node representing a subsentence or expression wrapped in parentheses.
     * 
     * @return
     * @throws RecognitionException
     */
    private AstNode match_arithmetic_expression_with_paren() throws RecognitionException {
	match(EgTokenCategory.LPAREN);
	final AstNode node = match_arithmetic_expression();
	match(EgTokenCategory.RPAREN);
	return node;
    }


    /**
     * Handles parsing of the <code><b>arithmetic_operation :: = (arithmetic_op term)*</b></code> portion of the rule <code><b>arithmetic_expression</b></code>. This include
     * correct handling of operation precedence when construction an AST.
     * 
     * Produces an AST node representing an operation (currently +, -, /, *) with all its children, which are defined recursively.
     * 
     * @param leftOperandNode
     * @return
     * @throws RecognitionException
     */
    private AstNode arithmetic_operation(final AstNode leftOperandNode) throws RecognitionException {
	// its okey not to have any operation as part of an arithmetic expression -- a single term is sufficient
	// thus need to check whether the next token actually represents an arithmetic operation
	if (isArithmeticOpNext()) {
	    final AstNode node = arithmetic_op();
	    final AstNode rightNode = term();
	    final EgTokenCategory cat = EgTokenCategory.byIndex(node.getToken().category.getIndex());

	    // anything that follows PLUS has higher precedence of execution and thus should be represented by a separate AST node
	    // for example, 5 + 2 * 5, 5 + 6 - 2
	    if (cat == EgTokenCategory.PLUS) {
		final AstNode rightOperandNode = arithmetic_operation(rightNode);
		return arithmetic_operation(node.addChild(leftOperandNode).addChild(rightOperandNode));
	    }
	    // anything that follows MINUS except PLUS has higher order of execution and thus should be represented by a separate AST node
	    // for example, 5 - 2 * 2, but in 5 - 4 + 3 token "-" has higher precedence
	    final EgTokenCategory next = lookahead();
	    if (cat == EgTokenCategory.MINUS && (next == EgTokenCategory.MULT || next == EgTokenCategory.DIV)) { // look ahead to determine if the next token is an operation of higher precedence
		final AstNode rightOperandNode = arithmetic_operation(rightNode);
		return arithmetic_operation(node.addChild(leftOperandNode).addChild(rightOperandNode));
	    }

	    return arithmetic_operation(node.addChild(leftOperandNode).addChild(rightNode));
	}

	return leftOperandNode;
    }

    /**
     * Produces an AST node that contains only a comparison operation token (i.e. without its arguments) in the current position. If the current position does not point to one of
     * the comparison operation tokens then an exception is thrown.
     * 
     * @return
     * @throws RecognitionException
     */
    private AstNode cmp_op() throws RecognitionException {
	final EgTokenCategory cat = EgTokenCategory.byIndex(tokens[position].category.getIndex());
	switch (cat) {
	// trying to match literal rule
	case LESS:
	case GREATER:
	case LE:
	case GE:
	case EQ:
	case NE:
	    return new AstNode(match(cat));
	default:
	    throw new NoViableAltException("Unexpected token '" + tokens[position].text + "' instead of a comparison operation.", tokens[position]);
	}
    }

    /**
     * Produces an AST node that contains only a logical operation token (i.e. without its arguments) in the current position. If the current position does not point to one of the
     * logical operation tokens then an exception is thrown.
     * 
     * @return
     * @throws RecognitionException
     */
    private AstNode logical_op() throws RecognitionException {
	final EgTokenCategory cat = EgTokenCategory.byIndex(tokens[position].category.getIndex());
	switch (cat) {
	// trying to match literal rule
	case AND:
	case OR:
	    return new AstNode(match(cat));
	default:
	    throw new NoViableAltException("Unexpected token '" + tokens[position].text + "' instead of a comparison operation.", tokens[position]);
	}
    }

    /**
     * Produces an AST node representing a single term that is current in the token stream. Any literal, left parenthesis and functions are considered to be valid terms.
     * <p>
     * The resultant node includes relevant children (if any).
     * 
     * @return
     * @throws RecognitionException
     */
    private AstNode term() throws RecognitionException {
	if (position >= tokens.length) {
	    throw new MissingTokenException("Missing token after token " + tokens[position - 1], tokens[position - 1]);
	}
	final EgTokenCategory cat = EgTokenCategory.byIndex(tokens[position].category.getIndex());
	switch (cat) {
	// trying to match literal rule
	case INT:
	case DECIMAL:
	case STRING:
	case DATE_CONST:
	    return literal(cat);
	    // trying to match property rule
	case SELF:
	    return self();
	    // trying to match sub-sentence
	case NAME:
	    return property();
	    // trying to match SELF rule
	case LPAREN:
	    return match_arithmetic_expression_with_paren(); //new AstNode(tokens[position]); // does not move position forward
	    // trying to match functions
	case AVG:
	case SUM:
	case MIN:
	case MAX:
	case COUNT:
	case DAY:
	case MONTH:
	case YEAR:
	case UPPER:
	case LOWER:
	case DAY_DIFF:
	    return function(cat);
	default:
	    throw new NoViableAltException("Unexpected token " + tokens[position], tokens[position]);
	}

    }

    /**
     * Produces an AST node that contains only an arithmetic operation token (i.e. without its arguments) in the current position. If the current position does not point to one of
     * the arithmetic operation tokens then an exception is thrown.
     * 
     * @return
     * @throws RecognitionException
     */
    private AstNode arithmetic_op() throws RecognitionException {
	final EgTokenCategory cat = EgTokenCategory.byIndex(tokens[position].category.getIndex());
	switch (cat) {
	// trying to match literal rule
	case PLUS:
	case MINUS:
	case MULT:
	case DIV:
	    return new AstNode(match(cat));
	default:
	    throw new NoViableAltException("Unexpected token '" + tokens[position].text + "' instead of an arithmetic operation.", tokens[position]);
	}
    }

    private boolean isArithmeticOpNext() {
	if (position >= tokens.length) {
	    return false;
	}
	final EgTokenCategory cat = EgTokenCategory.byIndex(tokens[position].category.getIndex());
	switch (cat) {
	// trying to match literal rule
	case PLUS:
	case MINUS:
	case MULT:
	case DIV:
	    return true;
	default:
	    return false;
	}
    }

    /**
     * A convenient method for looking ahead into the token stream without actually changing the current position.
     * 
     * @return
     */
    private EgTokenCategory lookahead() {
	if (position >= tokens.length) {
	    return EgTokenCategory.EOF;
	}
	return EgTokenCategory.byIndex(tokens[position].category.getIndex());
    }

    private AstNode literal(final EgTokenCategory cat) throws RecognitionException {
	return new AstNode(match(cat));
    }

    /**
     * Produces an AST node representing the name of some property, which could be in a property dot notation format.
     * 
     * @return
     * @throws RecognitionException
     */
    private AstNode property() throws RecognitionException {
	final Token propToken = match(EgTokenCategory.NAME);
	if (EgTokenCategory.SELF.name().equalsIgnoreCase(propToken.text)) {
	    throw new ReservedNameException("SELF is a keyword, should not be used as some property name.", propToken);
	}
	return new AstNode(propToken);
    }

    /**
     * Produces an AST node representing SELF keyword.
     * 
     * @return
     * @throws RecognitionException
     */
    private AstNode self() throws RecognitionException {
	return new AstNode(match(EgTokenCategory.SELF)) {
	    @Override
	    protected boolean canBeAddedTo(final AstNode intendedParentNode, final StringBuilder reason) {
		final boolean originalResult = super.canBeAddedTo(intendedParentNode, reason);
		if (originalResult && intendedParentNode.getToken().category != EgTokenCategory.COUNT) {
		    reason.append("Token " + EgTokenCategory.SELF + " can only be used as part of " + EgTokenCategory.COUNT + ".");
		    return false;
		}
		return originalResult;
	    }
	};
    }

    /**
     * Produces an AST node based on the token category, which is expected to be one of the supported functions. The resultant AST node has all its relevant children determined
     * recursively.
     * 
     * @param cat
     * @return
     * @throws RecognitionException
     */
    private AstNode function(final EgTokenCategory cat) throws RecognitionException {
	switch (cat) {
	// trying to match single argument function
	case AVG:
	case SUM:
	case MIN:
	case MAX:
	case COUNT:
	case DAY:
	case MONTH:
	case YEAR:
	case UPPER:
	case LOWER:
	    return new AstNode(match(cat)).addChild(match_arithmetic_expression_with_paren());
	    // trying to match two argument function
	case DAY_DIFF:
	    final AstNode node = new AstNode(match(EgTokenCategory.DAY_DIFF));
	    match(EgTokenCategory.LPAREN);
	    final AstNode leftArgNode = match_arithmetic_expression();
	    match(EgTokenCategory.COMMA);
	    final AstNode rightArgNode = match_arithmetic_expression();
	    match(EgTokenCategory.RPAREN);
	    return node.addChild(leftArgNode).addChild(rightArgNode);
	default:
	    throw new NoViableAltException("Could not parse starting from token " + tokens[position] + " at position " + position, tokens[position]);
	}

    }

    /**
     * Tries to match the token of a given category. Moves the position in the token steam after the matched token and returns the matched token for further processing.
     * 
     * @param cat
     * @return
     * @throws RecognitionException
     */
    private Token match(final EgTokenCategory cat) throws RecognitionException {
	// check if the current position still points to some token in the stream
	if (position >= tokens.length) {
	    throw new NoViableAltException("Expecting token " + cat + ", but found end of input.", tokens[position - 1]);
	}

	// retrieve the token to be returned
	final Token token = tokens[position];

	// just in case ensure that the retrieved token matches the requested category
	if (token.category.getIndex() != cat.index) {
	    throw new MismatchedTokenException("Could not match " + token + " to category " + cat.toString(), token);
	}

	// move to the next position and return the token
	position++;
	return token;
    }

    /**
     * Returns the current position in the token stream.
     * 
     * @return
     */
    public int getPosition() {
	return position;
    }
}
