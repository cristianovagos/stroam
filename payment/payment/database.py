import sqlite3, sys

from flask import current_app, g

#########################
## Database Object     ##
#########################

def get_db():
    if 'db' not in g:
        g.db = sqlite3.connect(
            current_app.config['DATABASE'],
            detect_types=sqlite3.PARSE_DECLTYPES
        )
        g.db.row_factory = sqlite3.Row

    return g.db


def close_db(e=None):
    db = g.pop('db', None)

    if db is not None:
        db.close()


#########################
## Database CRUD       ##
#########################

def insert(table, fields=(), values=()):
    '''
        Inserts a row in a table on database
    '''
    cur = get_db().cursor()

    query = 'INSERT INTO %s (%s) VALUES (%s)' % (
        table,
        ', '.join(fields),
        ', '.join(['?'] * len(values))
    )

    cur.execute(query, values)
    get_db().commit()

    id = cur.lastrowid
    cur.close()

    return id

def exists(table, columns, values):
    '''
        Checks if row exists on Database
    '''
    if isinstance(columns, str):
        columns = [columns]

    if isinstance(values, str):
        values = [values]

    cur = get_db().cursor()

    query = 'SELECT EXISTS(SELECT 1 FROM %s WHERE %s %s LIMIT 1);' % (
        table,
        '= ? AND '.join(columns),
        '= ?'
    )

    result = cur.execute(query, values).fetchone()[0]
    cur.close()

    return result

def get(table, column, value):
    '''
        Returns Row from database
    '''

    cur = get_db().cursor()

    query = 'SELECT * FROM %s WHERE %s=\"%s\" LIMIT 1' % (
        table,
        column,
        value
    )

    result = cur.execute(query).fetchone()
    cur.close()

    return result

def get_all(table, column, value):
    '''
        Returns Rows from database
    '''

    cur = get_db().cursor()

    query = 'SELECT * FROM %s WHERE %s=\"%s\"' % (
        table,
        column,
        value
    )

    result = cur.execute(query).fetchall()
    cur.close()

    return result
