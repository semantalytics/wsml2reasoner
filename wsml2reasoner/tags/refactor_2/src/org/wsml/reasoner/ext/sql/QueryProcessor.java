/*
 * Copyright (C) 2007 Digital Enterprise Research Institute (DERI), 
 * Leopold-Franzens-Universitaet Innsbruck, Technikerstrasse 21a, 
 * A-6020 Innsbruck. Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.wsml.reasoner.ext.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Accept and manipulate a 'SQL-like' query expression conforming to
 * WSML-Flight-A syntax.
 * 
 * SELECT X1 , . . . , Xn FROM ontology WHERE wsmlQuery
 * 
 * where wsmlQuery is a WSML-Flight query, X1 - Xn are a subset of the variables
 * appearing in wsmlQuery and ontology is an ontology confirming to the
 * WSML-Flight variant.
 * 
 * For example:
 * 
 * SELECT ?actor LIKE baz%, count(?pe), FROM
 * "http://www.somewhere.org/somedomainn#someBusinessProcess" WHERE ?pe [
 * generatedBy hasValue ? actor ] memberOf evo#successfulExecutionEvent GROUP BY
 * ?actor ORDER BY count(?pe) HAVING count(?pe) > 0
 * 
 * The SQL statement required to retrieve the inermediate results will look like
 * this:
 * 
 * SELECT C_actor, count(C_pe), FROM TABLE_NAME WHERE C_actor LIKE 'baz%' GROUP
 * BY C_actor ORDER BY COUNT(C_pe) HAVING COUNT(C_pe) > 0
 */
public class QueryProcessor {
    /**
     * Constructor. Accept the query and do basic processing.
     * 
     * @param sqlLikeQuery
     *            The SQL like query (containing the WSML query)
     */
    public QueryProcessor(String sqlLikeQuery) throws QueryFormatException {
        mSqlLikeQuery = sqlLikeQuery;

        parse();
    }

    /**
     * Override from Object.
     * 
     * @return The original SQL-like query
     */
    public String toString() {
        return mSqlLikeQuery;
    }

    /**
     * Get the ontology IRI from the FROM clause.
     * 
     * @return The ontology IRI.
     */
    public String getOntologyIRI() {
        return mOntologyIRI;
    }

    /**
     * Get the WSML query from the WHERE clause.
     * 
     * @return The WSML-Flight query.
     */
    public String getWsmlQuery() {
        ArrayList<String> sequence = mClauseTokens.get(WHERE);

        Iterator<String> it = sequence.iterator();
        it.next();

        return concatenate(it, new TokenAdapter());
    }

    /**
     * For the given table name, convert the WSML-Flight-A query in to the full
     * SQL query required to read the intermediate results from the WSML-Flight
     * query.
     * 
     * @param tableName
     * @return
     */
    public String constructSqlQueryWithColumnNamesSubstitutedForVariables(String tableName) {
        StringBuilder buffer = new StringBuilder();

        buffer.append(makeSqlSelectClause()).append(SPACE);

        buffer.append(FROM).append(SPACE).append(tableName).append(SPACE);

        if (mLikes.size() > 0) {
            buffer.append(makeSqlWhereClause()).append(SPACE);
        }

        buffer.append(concatenateVariablesToColumns(mClauseTokens.get(GROUP))).append(SPACE);
        buffer.append(concatenateVariablesToColumns(mClauseTokens.get(HAVING))).append(SPACE);
        buffer.append(concatenateVariablesToColumns(mClauseTokens.get(ORDER))).append(SPACE);
        buffer.append(concatenateVariablesToColumns(mClauseTokens.get(LIMIT))).append(SPACE);
        buffer.append(concatenateVariablesToColumns(mClauseTokens.get(OFFSET)));

        return buffer.toString();
    }

    /**
     * Parse the WSML-Flight-A query.
     * 
     * @throws QueryFormatException
     *             If the syntax of the query is invalid.
     */
    private void parse() throws QueryFormatException {
        // Break in to tokens
        tokenise(mSqlLikeQuery);

        // Some simple post parsing validation.
        validate();
    }

    /**
     * A functor base class for use when concatenating collection of string
     * token.
     */
    static class TokenAdapter {
        String transform(String token) {
            return token;
        }
    }

    /**
     * A functor that converts any variable names found in to sql column name
     * equivalents.
     */
    class VariableToColumnNameAdapter extends TokenAdapter {
        String transform(String token) {
            return convertVariableToColumnName(token);
        }
    }

    /**
     * Convert provided tokens that match a variable name in to a SQL column
     * name, otherwise leave alone.
     * 
     * @param expression
     * @return
     */
    private static String convertVariableToColumnName(String expression) {
        if (isVariable(expression))
            return expression.substring(1);
        else
            return expression;
    }

    /**
     * Format the SELECT clause for the SQL query.
     * 
     * @return The clause
     */
    private String makeSqlSelectClause() {
        StringBuilder buffer = new StringBuilder();

        buffer.append(SELECT).append(SPACE);

        boolean first = true;
        for (SelectExpression expression : mSelectExpressions) {
            if (first)
                first = false;
            else
                buffer.append(COMMA).append(SPACE);

            String token = convertVariableToColumnName(expression.mExpression);
            if (expression.mAggregate == null)
                buffer.append(token);
            else {
                buffer.append(expression.mAggregate).append('(').append(token).append(')');
                buffer.append(generateAlias(expression));
            }
        }

        return buffer.toString();
    }

    /**
     * Constructs an alias for select expressions which include an aggregate.
     * This is needed in order to get meaningful column names in result sets
     * which include aggregates.
     * 
     * E.g. SUM(X) => returns AS SUM_X which can be appended to the original
     * expression: SUM(X) AS SUM_X. This results in SUM_X as column name in the
     * SQL ResultSet.
     * 
     * COUNT(*) results in COUNT_STAR.
     * 
     * @param expression
     * @return An suitable alias as string.
     */
    private String generateAlias(SelectExpression expression) {
        assert expression != null;

        if (expression.mAggregate == null) {
            throw new IllegalArgumentException("Aliases are only generated for aggregate expressions.");
        }

        StringBuilder buffer = new StringBuilder();
        String aggregate = expression.mAggregate.toLowerCase();
        String reformat = aggregate.substring(0, 1).toUpperCase() + aggregate.substring(1);
        buffer.append(SPACE).append(AS).append(SPACE).append(reformat + "Of");

        if (expression.mAggregate.equals(COUNT)) {
            buffer.append(convertVariableToColumnName(expression.mExpression.replaceAll("\\*", "STAR")));
        }
        else {
            buffer.append(expression.mExpression);
        }

        return buffer.toString();
    }

    /**
     * Format the WHERE clause for the SQL query.
     * 
     * @return The clause
     */
    private String makeSqlWhereClause() {
        StringBuilder buffer = new StringBuilder();

        buffer.append(WHERE).append(SPACE);

        boolean first = true;
        for (Like like : mLikes) {
            if (first)
                first = false;
            else
                buffer.append(COMMA).append(SPACE);

            buffer.append(convertVariableToColumnName(like.mVariable)).append(SPACE).append(LIKE).append(SPACE).append(QUOTE).append(like.mPattern).append(QUOTE);
        }

        return buffer.toString();
    }

    private String concatenate(Iterator<String> iterator, TokenAdapter adapter) {
        StringBuilder buffer = new StringBuilder();

        boolean first = true;

        while (iterator.hasNext()) {
            if (first)
                first = false;
            else
                buffer.append(SPACE);

            buffer.append(adapter.transform(iterator.next()));
        }

        return buffer.toString();
    }

    private String concatenateVariablesToColumns(ArrayList<String> tokens) {
        if (tokens == null)
            return "";
        else
            return concatenate(tokens.iterator(), new VariableToColumnNameAdapter());
    }

    private void endToken() throws QueryFormatException {
        if (mToken.length() > 0) {
            processToken(mToken.toString());
            mToken = new StringBuilder();
        }
    }

    /**
     * Process character events (ignoring string literals).
     * 
     * @param ch
     */
    private void processChars(char ch) throws QueryFormatException {
        switch (ch) {
        case ' ':
        case '\t':
        case '\r':
        case '\n':
            // Discard and end token
            endToken();
            break;

        case '[':
        case ']':
        case '(':
        case ')':
        case ',':
            endToken();
            mToken.append(ch);
            endToken();
            break;

        case '?':
            endToken();
            mToken.append(ch);
            break;

        default:
            mToken.append(ch);
        }
    }

    /**
     * Process character events, but filter out single and doubly quoted strings
     * as separate tokens.
     * 
     * @param ch
     *            The current character to process.
     */
    private void processCharsPreserveStringLiterals(char ch) throws QueryFormatException {
        switch (mStringLiteral) {
        case 0: // Not inside a string literal
            if (ch == QUOTE) {
                endToken();
                mStringLiteral = 1;
                mToken.append(ch);
            }
            else if (ch == DQUOTE) {
                endToken();
                mStringLiteral = 2;
                mToken.append(ch);
            }
            else
                processChars(ch);
            break;

        case 1:
            if (ch == QUOTE) {
                mStringLiteral = 0;
                mToken.append(ch);
                endToken();
            }
            else
                mToken.append(ch);
            break;

        case 2:
            if (ch == DQUOTE) {
                mStringLiteral = 0;
                mToken.append(ch);
                endToken();
            }
            else
                mToken.append(ch);
        }
    }

    /**
     * Traverse the string and break in to bits.
     * 
     * @param query
     *            The query string (WSML-Flight-A)
     */
    private void tokenise(String query) throws QueryFormatException {
        for (char ch : query.toCharArray()) {
            processCharsPreserveStringLiterals(ch);
        }
        endToken();
        endClause();
    }

    private void validate() throws QueryFormatException {
        if (mSelectExpressions.size() == 0)
            throw new QueryFormatException("There is no 'SELECT' clause in the query.");

        if (mOntologyIRI == null)
            throw new QueryFormatException("There is no 'FROM' clause in the query.");

        if (mClauseTokens.get(WHERE) == null)
            throw new QueryFormatException("There is no WHERE clause.");
    }

    private void processSelectSubClause(ArrayList<String> subClause) throws QueryFormatException {
        if (isAggregate(subClause.get(0))) {
            try {
                if (subClause.size() != 4)
                    throw new Exception();
                if (!subClause.get(1).equals("("))
                    throw new Exception();
                if (!subClause.get(3).equals(")"))
                    throw new Exception();

                mSelectExpressions.add(new SelectExpression(subClause.get(2), subClause.get(0)));
            }
            catch (Exception e) {
                throw new QueryFormatException("Badly formed aggregate function in SELECT clause");
            }
        }
        // Remove LIKE expressions
        else if (subClause.size() == 3) {
            try {
                if (!subClause.get(1).equals(LIKE))
                    throw new Exception();

                String variable = subClause.get(0);
                String pattern = subClause.get(2);

                if (!isVariable(variable) || isVariable(pattern))
                    throw new Exception();

                char p = pattern.charAt(0);
                if (p == '(' || p == ')')
                    throw new Exception();
                mLikes.add(new Like(variable, pattern));

                mSelectExpressions.add(new SelectExpression(variable, null));
            }
            catch (Exception e) {
                throw new QueryFormatException("'LIKE' expressions must be of the form: '?variable LIKE pattern'");
            }
        }

        // Simple variable/constant expression
        else if (subClause.size() == 1) {
            mSelectExpressions.add(new SelectExpression(subClause.get(0), null));
        }
    }

    private void processSelectClause(ArrayList<String> sequence) throws QueryFormatException {
        // Simple validation first. Must later traverse the expression list
        // looking for commas
        // in the correct places and 'LIKE' sub expressions.
        // Can also ensure that open and closing braces match around COUNT( expr
        // ), SUM( ), etc
        if (sequence.size() < 2)
            throw new QueryFormatException("There are no expressions in the SELECT clause.");

        // Break in to sub-clauses
        try {
            ArrayList<String> subClause = new ArrayList<String>();

            for (int i = 1; i < sequence.size(); ++i) {
                String token = sequence.get(i);
                if (token.charAt(0) == COMMA) {
                    if (subClause.size() == 0)
                        throw new Exception();

                    processSelectSubClause(subClause);
                    subClause = new ArrayList<String>();
                }
                else {
                    subClause.add(token);
                }
            }

            if (subClause.size() > 0)
                processSelectSubClause(subClause);
        }
        catch (Exception e) {
            throw new QueryFormatException("'SELECT' cluase should have the form: SELECT ?variable | aggregate( ?variable ) | ?variable LIKE pattern, ...");
        }
    }

    private void processFromClause(ArrayList<String> sequence) throws QueryFormatException {
        if (sequence.size() != 3 || !sequence.get(1).equals("_") || sequence.get(2).charAt(0) != DQUOTE)
            throw new QueryFormatException("The FROM clause is badly formatted, it should be of the form 'FROM _\"<ontology IRI>\"");

        mOntologyIRI = sequence.get(1) + sequence.get(2);
    }

    private void processWhereClause(ArrayList<String> sequence) throws QueryFormatException {
        if (sequence.size() < 2)
            throw new QueryFormatException("The WHERE clause is badly formatted, it should be of the form 'WHERE <WSML-Flight query>");

        mClauseTokens.put(sequence.get(0), mCurrentClause);
    }

    private void processOrderClause(ArrayList<String> sequence) throws QueryFormatException {
        if (sequence.size() < 2 || !sequence.get(1).equals(BY)) {
            throw new QueryFormatException("'ORDER' must be followed by 'BY'");
        }

        if (sequence.size() == 2) {
            throw new QueryFormatException("The ORDER BY clause is empty.");
        }

        mClauseTokens.put(sequence.get(0), mCurrentClause);
    }

    private void processGroupClause(ArrayList<String> sequence) throws QueryFormatException {
        if (sequence.size() < 2 || !sequence.get(1).equals(BY)) {
            throw new QueryFormatException("'GROUP' must be followed by 'BY'");
        }

        if (sequence.size() == 2) {
            throw new QueryFormatException("The GROUP BY clause is empty.");
        }

        mClauseTokens.put(sequence.get(0), mCurrentClause);
    }

    private void processHavingClause(ArrayList<String> sequence) throws QueryFormatException {
        if (sequence.size() == 1)
            throw new QueryFormatException("The HAVING clause is empty.");

        mClauseTokens.put(sequence.get(0), mCurrentClause);
    }

    private void processLimitClause(ArrayList<String> sequence) throws QueryFormatException {
        final String message = "The LIMIT clause must have exacty one INTEGER value >= 0";

        if (sequence.size() != 2)
            throw new QueryFormatException(message);

        int value;
        try {
            value = Integer.parseInt(sequence.get(1));
        }
        catch (Exception e) {
            throw new QueryFormatException(message);
        }

        if (value < 0)
            throw new QueryFormatException(message);

        mClauseTokens.put(sequence.get(0), mCurrentClause);
    }

    private void processOffsetClause(ArrayList<String> sequence) throws QueryFormatException {
        final String message = "The OFFSET clause must have exacty one INTEGER value >= 0";

        if (sequence.size() != 2)
            throw new QueryFormatException(message);

        int value;
        try {
            value = Integer.parseInt(sequence.get(1));
        }
        catch (Exception e) {
            throw new QueryFormatException(message);
        }

        if (value < 0)
            throw new QueryFormatException(message);

        mClauseTokens.put(sequence.get(0), mCurrentClause);
    }

    private void processClause(ArrayList<String> sequence) throws QueryFormatException {
        String first = sequence.get(0);

        if (first.equals(SELECT)) {
            processSelectClause(sequence);
        }
        else if (first.equals(FROM)) {
            processFromClause(sequence);
        }
        else if (first.equals(WHERE)) {
            processWhereClause(sequence);
        }
        else if (first.equals(ORDER)) {
            processOrderClause(sequence);
        }
        else if (first.equals(GROUP)) {
            processGroupClause(sequence);
        }
        else if (first.equals(HAVING)) {
            processHavingClause(sequence);
        }
        else if (first.equals(LIMIT)) {
            processLimitClause(sequence);
        }
        else if (first.equals(OFFSET)) {
            processOffsetClause(sequence);
        }
        else
            throw new QueryFormatException("SQL-like query must begin with 'SELECT'");
    }

    /**
     * Called at the end of a clause in the SQL-like query. Finishes the
     */
    private void endClause() throws QueryFormatException {
        if (mCurrentClause.size() > 0) {
            processClause(mCurrentClause);
            mCurrentClause = new ArrayList<String>();
        }
    }

    /**
     * Process the token just parsed.
     * 
     * @param token
     *            The token to process.
     * @throws QueryFormatException
     *             If this token can is syntactically incorrect in this position
     *             in the query.
     */
    private void processToken(String token) throws QueryFormatException {
        // Careful with this
        if (isSqlKeyword(token))
            token = token.toUpperCase();

        if (isClauseKeyword(token)) {
            endClause();
            String upperToken = token.toUpperCase();

            mCurrentClause.add(upperToken);
        }
        else {
            mCurrentClause.add(token);
        }
    }

    /**
     * Indicates if a token is a WSML-Flight variable.
     * 
     * @param token
     *            The token to examine.
     * @return true, if it is a variable.
     */
    private static boolean isVariable(String token) {
        return token.charAt(0) == QUESTION;
    }

    /**
     * Indicate if the given token is a SQL keyword.
     * 
     * @param token
     *            true If the supplied token is a SQL keyword.
     * @return
     */
    private static boolean isSqlKeyword(String token) {
        for (String keyword : ALL_SQL_KEYWORDS)
            if (token.equalsIgnoreCase(keyword))
                return true;

        return false;
    }

    /**
     * Indicates of the supplied token introduces a new clause in the SQL-like
     * query.
     * 
     * @param token
     *            The token just parsed.
     * @return true If start of new clause.
     */
    private static boolean isClauseKeyword(String token) {
        return token.equalsIgnoreCase(SELECT) || token.equalsIgnoreCase(FROM) || token.equalsIgnoreCase(WHERE) || token.equalsIgnoreCase(GROUP) || token.equalsIgnoreCase(ORDER) || token.equalsIgnoreCase(HAVING) || token.equalsIgnoreCase(LIMIT) || token.equalsIgnoreCase(OFFSET);
    }

    /**
     * Indicates of the supplied token is a SQL aggregate function.
     * 
     * @param token
     *            The token just parsed.
     * @return true If the token is an aggregate function.
     */
    private static boolean isAggregate(String token) {
        return token.equalsIgnoreCase(COUNT) || token.equalsIgnoreCase(MIN) || token.equalsIgnoreCase(MAX) || token.equalsIgnoreCase(SUM) || token.equalsIgnoreCase(AVG);
    }

    /** Original query as passed to the constructor. */
    private String mSqlLikeQuery;

    /** The ontology IRI from the FROM clause. */
    private String mOntologyIRI;

    /**
     * Holder for select expressions, of the form either: ?variable OR
     * <aggregate>( ?variable )
     */
    static class SelectExpression {
        SelectExpression(String expression, String aggregate) {
            mExpression = expression;
            mAggregate = aggregate;
        }

        final String mExpression;

        final String mAggregate;
    }

    /** The list of select expressions. */
    private ArrayList<SelectExpression> mSelectExpressions = new ArrayList<SelectExpression>();

    /**
     * Container for 'variable LIKE pattern' expressions.
     */
    static class Like {
        Like(String variable, String pattern) {
            mVariable = variable;
            mPattern = pattern;
        }

        final String mVariable;

        final String mPattern;
    }

    /** List of LIKE sub-clauses in SELECT clause. */
    private ArrayList<Like> mLikes = new ArrayList<Like>();

    /** The map of clauses that makes up this SQL-like query. */
    private Map<String, ArrayList<String>> mClauseTokens = new HashMap<String, ArrayList<String>>();

    /** The currently parsing token. */
    private StringBuilder mToken = new StringBuilder();

    /** The currently parsing clause. */
    private ArrayList<String> mCurrentClause = new ArrayList<String>();

    /** A flag to indicate what kind of string literal is currently parsing. */
    private int mStringLiteral = 0;

    private static final String SELECT = "SELECT";

    private static final String FROM = "FROM";

    private static final String WHERE = "WHERE";

    private static final String GROUP = "GROUP";

    private static final String ORDER = "ORDER";

    private static final String HAVING = "HAVING";

    private static final String LIMIT = "LIMIT";

    private static final String OFFSET = "OFFSET";

    private static final String BY = "BY";

    private static final String LIKE = "LIKE";

    private static final String AS = "AS";

    private static final String COUNT = "COUNT";

    private static final String MIN = "MIN";

    private static final String MAX = "MAX";

    private static final String SUM = "SUM";

    private static final String AVG = "AVG";

    private static final String[] ALL_SQL_KEYWORDS = { SELECT, FROM, WHERE, GROUP, ORDER, HAVING, LIMIT, OFFSET, BY, LIKE, COUNT, MIN, MAX, SUM, AVG, AS };

    private static final char QUOTE = '\'';

    private static final char DQUOTE = '"';

    private static final char SPACE = ' ';

    private static final char COMMA = ',';

    private static final char QUESTION = '?';
}
