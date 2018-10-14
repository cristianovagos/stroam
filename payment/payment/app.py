'''
António Sérgio Silva
asergiosilv@gmail.com https://asergio.pw
Engenharia de Servicos 2018/19 MEC:76678 asergio@ua.pt
'''

from flask import Flask, request, jsonify, render_template, redirect, url_for, session
from util import *
from apispec import APISpec
from apispec.ext.flask import FlaskPlugin
import database as db
import secrets, json

app = Flask(__name__)
app.config.from_pyfile('config.py')

# Create an APISpec
spec = APISpec(
    title='StrongPay',
    base_path='http://127.0.0.1:5000/',
    version='1.0.0',
    openapi_version='3.0.0',
    plugins=[
        FlaskPlugin()
    ]
)


@app.route('/')
def index():
    ''' Index page, simply returns a login form for now '''
    # Checking if it came from a error redirect
    if request.args.get('error'):
        return render_template('index.html', error = request.args.get('error')), 400

    if session.get('user_id'):
        return 'i know you are logged in, i just don\'t have a page yet'

    return render_template('index.html'), 200


@app.route('/docs')
def docs():
    ''' Docs page '''
    return render_template('doc.html'), 200


@app.route('/login', methods=['POST'])
def login():
    ''' Login (create session) '''

    # Cleaning URL to get args being used by the next url
    args = request.args.to_dict()
    args.pop('next', None)
    args.pop('error', None)

    # Checking if there is already a valid session
    if 'user_id' in session and db.exists('CLIENT', 'id', session['user_id']):
        redirect(url_for(request.args.get('next'), **args))

    # request.form looks ugly and takes too much space...
    param = request.form
    keys = [k for k in param.keys()]
    required_keys = ['email', 'pass']

    # Checking for required parameters
    if not param or not check_keys(required_keys, keys):
        return redirect(url_for(request.args.get('next'), \
                                error = error_message('login_fails'),
                                **args))

    # Super insecure authentication, don't try this outside localhost kids
    if db.exists('USER', ['email', 'password'], [ param['email'], param['pass'] ] ):
        session['user_id'] = db.get('USER', 'email', param['email'])['id']
    else:
        return redirect(url_for(request.args.get('next'), \
                            error = error_message('wrong_pass'), \
                            **args))

    # Returning to origin
    return redirect(url_for(request.args.get('next'), **args))

@app.route('/GetCheckoutDetails', methods=['GET'])
def get_checkout():
    ''' GetCheckoutDetails
    ---
    get:
        description: Gets checkout details such as who paid and billing address
        tags:
            - Payment
        parameters:
            - in: path
              name: checkout_token
              schema:
                type: string
              required: true
              description: Checkout token given by create_checkout
        responses:
            200:
                description: A JSON containing buyer information and checkout status (not defined yet)
                content:
                    application/json:
                      schema:
                        properties:
                          NAME:
                            type: string
                          BUYER_ID:
                            type: string
            400:
                description: A JSON containing a ERROR that indentifies the problem
                content:
                    application/json:
                      schema:
                        properties:
                          ERROR:
                            type: string
    '''
    # Sturb api just for testing
    return jsonify({'NAME': 'ZE MANEL', 'ID' : 'BUYERID123'}), 200

@app.route('/ExecuteCheckout', methods=['GET'])
def execute_checkout():
    ''' ExecuteCheckout
    ---
    get:
        description: Confirm and execute payment. Returns Success or Failure.
        tags:
            - Payment
        parameters:
            - in: path
              name: checkout_token
              schema:
                type: string
              required: true
              description: Checkout token given by create_checkout
            - in: path
              name: buyer_id
              schema:
                type: string
              required: true
              description: Buyer ID given by /GetCheckoutDetails
        responses:
            200:
                description: A JSON containing result of payment
                content:
                    application/json:
                      schema:
                        properties:
                          SUCCESS:
                            type: boolean
            400:
                description: A JSON containing a ERROR that indentifies the problem
                content:
                    application/json:
                      schema:
                        properties:
                          ERROR:
                            type: string
    '''
    # Sturb api just for testing
    return jsonify({'SUCCESS': True}), 200

@app.route('/CreateCheckout', methods=['POST'])
def create_checkout():
    ''' CreateCheckout
    ---
    post:
        description: Creates a checkout
        tags:
            - Payment
        requestBody:
            required: true
            content:
              application/x-www-form-urlencoded:
                schema:
                  type: object
                  properties:
                    AMOUNT:
                      type: double
                      description: Amount to be paid by the client
                    MERCHANT:
                      type: string
                      description: Token that indentifies the merchant
                    RETURN_URL:
                      type: string
                      description: URL to where the client in redirect if the payment is successful
                    CANCEL_URL:
                      type: string
                      description: URL to where the client in redirect if the payment is cancelled
                  required:
                    - AMOUNT
                    - MERCHANT
                    - RETURN_URL
                    - CANCEL_URL
        responses:
            201:
                description: A JSON containing a TOKEN that indentifies the Checkout
                content:
                    application/json:
                      schema:
                        properties:
                          TOKEN:
                            type: string
            400:
                description: A JSON containing a ERROR that indentifies the problem
                content:
                    application/json:
                      schema:
                        properties:
                          ERROR:
                            type: string
    '''
    # request.form looks ugly and takes too much space...
    param = request.form
    keys = [k for k in param.keys()]
    required_keys = ['AMOUNT', 'MERCHANT', 'RETURN_URL', 'CANCEL_URL']

    # Checking for required parameters
    if not param or not check_keys(required_keys, keys):
        return jsonify({'ERROR': 'Invalid format or parameters missing.'}), 400

    # Cheking if URI are valid
    if not uri_validator(param['RETURN_URL']) or not uri_validator(param['CANCEL_URL']):
        return jsonify({'ERROR': 'RETURN or CANCEL URI with invalid format.'}), 400

    # Checking if amount is a valid number
    if not is_number(param['AMOUNT']):
        return jsonify({'ERROR': 'AMOUNT not valid.'}), 400

    # Checking if merchant exists
    if not db.exists('MERCHANT', 'id', param['MERCHANT']):
        return jsonify({'ERROR': 'MERCHANT doesn\'t exist.'}), 400

    # Generating token and checking if it doesn't exist already
    while True:
        token = secrets.token_urlsafe(16)
        if not db.exists('CHECKOUT', 'id', token):
            break

    # Inserting new checkout to database
    try:
        validation = db.insert('CHECKOUT', \
            ('id', 'amount', 'return_url', 'cancel_url', 'merchant'), \
            tuple( [token] + [param[k] for k in keys] ) )
    except Exception as e:
        return jsonify({'ERROR': 'An error ocurred on the Database.'}), 500

    # Everything went well, returning token for new checkout
    return jsonify({'CHECKOUT_TOKEN': token}), 201

@app.route('/pay', methods=['GET'])
def pay():
    '''
        Payment page, the client comes to this page after clicking "Pay" on the merchant page
    '''

    # request.args looks ugly and takes too much space...
    args = request.args
    keys = [k for k in args.keys()]
    required_keys = ['checkout_token']

    # Checking for required arguments
    if not args or not check_keys(required_keys, keys):
        return redirect(url_for('index', error = "Checkout not valid, please contact the responsible merchant."))

    # Getting row from database of the checkout
    checkout = db.get('CHECKOUT', 'id', args['checkout_token']);

    # Checking if checkout is valid
    if not checkout:
        return redirect(url_for('index', error = "Checkout not valid, please contact the responsible merchant."))

    login_form = True
    if session.get('user_id'):
        login_form = False

    error = False
    if request.args.get('error'):
        error = request.args.get('error')

    return render_template('pay.html', amount = str(checkout['AMOUNT']) + " €",
                                        login_form = login_form, error = error ), 200

@app.route('/proccess_payment', methods=['POST'])
def proccess_payment():
    '''
        After the client fills all the information the payment is proccessed
    '''

    # request.args looks ugly and takes too much space...
    args = request.args
    keys = [k for k in args.keys()]
    required_keys = ['checkout']

    # Checking for required arguments
    if not args or not check_keys(required_keys, keys):
        return redirect(url_for('index', error = "Checkout not valid, please contact the responsible merchant."))

    # Getting row from database of the checkout
    checkout = db.get('CHECKOUT', 'id', args['checkout']);

    # Checking if checkout is valid
    if not checkout:
        return redirect(url_for('index', error = "Checkout not valid, please contact the responsible merchant."))

    # TODO: Store Credit Card and mark checkout as paid

    # Redirect to the URL given by the merchant
    return redirect(checkout["RETURN_URL"])

### Generating openapi json file for swagger
with app.test_request_context():
    spec.add_path(view=create_checkout)
    spec.add_path(view=get_checkout)
    spec.add_path(view=execute_checkout)

with open('static/swagger.json', 'w') as f:
    json.dump(spec.to_dict(), f)
###

if __name__ == '__main__':
    app.run(debug=True, port=5000) #run app in debug mode on port 5000
