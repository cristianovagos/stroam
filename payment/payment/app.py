'''
António Sérgio Silva
asergiosilv@gmail.com https://asergio.pw
Engenharia de Servicos 2018/19 MEC:76678 asergio@ua.pt
'''

from flask import Flask, request, jsonify, render_template
from urllib.parse import urlparse
import database as db
import secrets

app = Flask(__name__)
app.config.from_pyfile('config.py')

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/CreateCheckout', methods=['POST'])
def create_checkout():
    '''
        /CreateCheckout - Creates a checkout for later to be paid by a client

        Arguments:
            AMOUNT - amount the client as to pay
            RETURN_URL - URL the client is redirected to after paying
            CANCEL_URL - URL the client is redirected to in case he cancels
            MERCHANT - ID of merchant
    '''
    param = request.form

    # Checking for required parameters
    if not param or not check_parameters(['AMOUNT','RETURN_URL', \
                'CANCEL_URL', 'MERCHANT'], param):
        return jsonify({'ERROR': 'Invalid format or parameters missing.'}), 400

    # Cheking if URI are valid
    if not uri_validator(param['RETURN_URL']) or not uri_validator(param['CANCEL_URL']):
        return jsonify({'ERROR': 'RETURN or CANCEL URI with invalid format.'}), 400

    # Checking if amount is a valid number
    if not is_number(param['AMOUNT']):
        return jsonify({'ERROR': 'AMOUNT not valid.'}), 400

    # Generating token
    token = secrets.token_urlsafe(16)

    try:
        validation = insert('CHECKOUT', \
            ('id', 'amount', 'return_url', 'cancel_url', 'merchant'), \
            (token, param['AMOUNT'] , param['RETURN_URL'], param['CANCEL_URL'], \
            param['MERCHANT']))
    except Exception as e:
        return jsonify({'ERROR': 'An error ocurred on the Database.'}), 500

    # Everything went well, returning token for new checkout
    return jsonify({'CHECKOUT_TOKEN': token}), 201

def insert(table, fields=(), values=()):
    '''
        Inserts a row in a table on database
    '''
    cur = db.get_db().cursor()

    query = 'INSERT INTO %s (%s) VALUES (%s)' % (
        table,
        ', '.join(fields),
        ', '.join(['?'] * len(values))
    )

    cur.execute(query, values)
    db.get_db().commit()

    id = cur.lastrowid
    cur.close()

    return id


def check_parameters(parameters, request):
    '''
        Checks if every required parameters is on the request
    '''
    for key in parameters:
        if key not in request:
            return False
    return True

def uri_validator(x):
    '''
        Checks if url x is valid
    '''
    try:
        result = urlparse(x)
        return all([result.scheme, result.netloc])
    except:
        return False

def is_number(s):
    '''
        Checks if s is a number
    '''
    try:
        float(s)
        return True
    except ValueError:
        return False

if __name__ == '__main__':
    app.run(debug=True, port=5000) #run app in debug mode on port 5000
