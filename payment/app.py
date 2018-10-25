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
        return render_template('index.html', error = error_message(request.args.get('error'))), 400

    if 'user_id' in session and db.exists('CLIENT', 'id', session['user_id']):
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
    keys = param.keys()
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

@app.route('/api/v1/Checkout', methods=['GET'])
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
                        type: object
                        properties:
                          CHECKOUT:
                            type: object
                            properties:
                                ID:
                                    type : string
                                    description : Checkout's ID.
                                AMOUNT:
                                    type : number
                                    description : Total amount of checkout.
                                STATUS:
                                    type : string
                                    description : Current status of checkout.
                                CURRENCY:
                                    type : string
                                    description : Currency used on checkout.
                                PAID_WITH:
                                    type : string
                                    description : Partial part of credit card used for the purchase.
                          BUYER:
                            type: object
                            properties:
                                ID:
                                    type : string
                                    description : Buyer ID.
                                NAME:
                                    type : string
                                    description : Buyer Name.
                                NIF:
                                    type : number
                                    description : Buyer NIF.
                          MERCHANT:
                             type: object
                             properties:
                                ID:
                                    type : string
                                    description : Merchant ID.
                                NAME:
                                    type : string
                                    description : Merchant Name.
                          ITEMS:
                              type: array
                              items:
                                  type: object
                                  properties:
                                     NAME:
                                        type : string
                                        description : Item name.
                                     PRICE:
                                        type : number
                                        description : Item price.
                                     QUANTITY:
                                        type : number
                                        description : Item quantity.
                                     URL:
                                        type : string
                                        description : Item url to merchant website.

            400:
                description: A JSON containing a ERROR that indentifies the problem
                content:
                    application/json:
                      schema:
                        properties:
                          ERROR:
                            type: string
    '''

    # request.args looks ugly and takes too much space...
    args = request.args
    keys = args.keys()
    required_keys = ['checkout_token']

    # Checking for required arguments
    if not args or not check_keys(required_keys, keys):
        return jsonify({'ERROR': error_message('invalid_request')}), 400

    # Getting row from database of the checkout
    checkout = db.get('CHECKOUT', 'id', args['checkout_token']);

    # Checking if checkout existed in database
    if not checkout:
        return jsonify({'ERROR': error_message('invalid_checkout')}), 400

    # Get buyer, items and merchant information from database
    buyer = db.get('CLIENT', 'id', checkout['paid_by'])
    items = db.get_all('ITEM', 'checkout', checkout['id'])
    merchant = db.get('MERCHANT', 'id', checkout['merchant'])
    billing_address = db.get('BILLING_ADDRESS', 'id', checkout['billing_address'])
    items = [ dict(i) for i in items]

    # Ignore this, simply making every key on dictionary uppercase because it looks good
    for i in items:
        i.pop('checkout')
        for k in list(i):
            i[k.upper()] = i.pop(k)

    # Building information
    info = {
            'CHECKOUT' : {'ID': checkout['id'], 'STATUS' : checkout['status'],
                            'AMOUNT' : checkout['amount'],
                            'CURRENCY' : checkout['currency']},
            'MERCHANT' : {'ID': merchant['id'], 'NAME': merchant['name']},
            'ITEMS'    : items
    }

    # Check if there is a BUYER to add
    if buyer:
        info['BUYER'] = {'ID' : buyer['id'], 'NAME' : buyer['name'],
                        'NIF': buyer['nif']};

    # Checking if there is credit card information to add
    if checkout['paid_with']:
        # Hiding credit card info
        credit_card = '*' * 12 + str(checkout['paid_with'])[-4:]
        info['CHECKOUT']['PAID_WITH'] = credit_card

    # Checking if there is billing address information to add
    if billing_address:
        info['BILLING_ADDRESS'] = { 'FIRST NAME': billing_address['first_name'],
                                    'LAST NAME': billing_address['last_name'],
                                    'COUNTRY' : billing_address['country'],
                                    'CITY' : billing_address['city'],
                                    'ADDRESS' : billing_address['address'],
                                    'POST-CODE' : billing_address['post_code'],
                                    'PHONE' : billing_address['phone']}

    # Returning information
    return jsonify(info), 200

@app.route('/api/v1/ExecuteCheckout', methods=['GET'])
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

    # request.args looks ugly and takes too much space...
    args = request.args
    keys = args.keys()
    required_keys = ['checkout_token', 'buyer_id']

    # Checking for required arguments
    if not args or not check_keys(required_keys, keys):
        return jsonify({'SUCCESS': False, 'ERROR': error_message('invalid_request')}), 400

    # Getting row from database of the checkout
    checkout = db.get('CHECKOUT', 'id', args['checkout_token'])

    if not checkout:
        return jsonify({'SUCCESS': False, 'ERROR': error_message('invalid_checkout')}), 400

    # Checking if checkout is ready to be executed
    if checkout['status'] != 'READY':
        return jsonify({'SUCCESS': False, 'ERROR': error_message('checkout_not_ready')}), 200

    try:
        db.update('CHECKOUT', ['status'],
                                ['PAID'],
                                'id', args['checkout_token'])
    except Exception as e:
        print(e)
        return jsonify({'SUCCESS': False, 'ERROR': error_message('db_error')}), 200

    # Everything went well, informing the merchant
    return jsonify({'SUCCESS': True}), 200


@app.route('/api/v1/Checkout', methods=['DELETE'])
def delete_checkout():
    ''' EditCheckout
    ---
    delete:
        description: Deletes the checkout
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
                description: A JSON containing result of the proccess
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
    # request.args looks ugly and takes too much space...
    args = request.args
    keys = args.keys()
    required_keys = ['checkout_token']

    # Checking for required arguments
    if not args or not check_keys(required_keys, keys):
        return jsonify({'ERROR': error_message('invalid_request')}), 400

    # Checking if checkout exists
    if not db.exists('CHECKOUT', 'id', args['checkout_token']):
        return jsonify({'ERROR': error_message('invalid_checkout')}), 400

    # Delete from database
    try:
        db.delete('CHECKOUT', 'id', args['checkout_token'])
    except Exception as e:
        print(e)
        return jsonify({'ERROR': error_message('db_error')}), 500

    # Everything went well
    return jsonify({'SUCCESS': True}), 200


@app.route('/api/v1/Checkout', methods=['POST','PUT'])
def create_checkout():
    ''' Create/Update Checkout
    ---
    put:
        description: Updates a checkout, all the information is replaced by the on given.
        tags:
            - Payment
        parameters:
            - in: path
              name: checkout_token
              schema:
                type: string
              required: true
              description: Checkout token given by create_checkout
        requestBody:
            required: true
            content:
              application/json:
                schema:
                  type: object
                  properties:
                    AMOUNT:
                      type: number
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
                    CURRENCY:
                      type: string
                      description: Three characthers currency code. Default value is 'EUR'. [https://www.xe.com/iso4217.php]
                      default : EUR
                    ITEMS:
                      type: array
                      items:
                            type : object
                            properties:
                                NAME:
                                    type : string
                                    description: Checkout item's name. Default value is "Item". This parameter is required if you fill any other item parameter.
                                    default : Item
                                PRICE:
                                    type : number
                                    description: Checkout item's price. Default value is the one given in 'AMOUNT'. This parameter is required if you fill any other item parameter.
                                QUANTITY:
                                    type : integer
                                    description: Checkout item's quantity. Default value is 1. This parameter is not required at any situation.
                                    default : 1
                                IMAGE:
                                    type : string
                                    description: Checkout item's image URL. It must be from your domain. This parameter is not required at any situation.
                                URL:
                                    type : string
                                    description: Checkout item's URL to your domain. This parameter is not required at any situation.
                  required:
                    - AMOUNT
                    - MERCHANT
                    - RETURN_URL
                    - CANCEL_URL
        responses:
            201:
                description: A JSON containing result of the proccess
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
    post:
        description: Creates a checkout
        tags:
            - Payment
        requestBody:
            required: true
            content:
              application/json:
                schema:
                  type: object
                  properties:
                    AMOUNT:
                      type: number
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
                    CURRENCY:
                      type: string
                      description: Three characthers currency code. Default value is 'EUR'. [https://www.xe.com/iso4217.php]
                      default : EUR
                    ITEMS:
                      type: array
                      items:
                            type : object
                            properties:
                                NAME:
                                    type : string
                                    description: Checkout item's name. Default value is "Item". This parameter is required if you fill any other item parameter.
                                    default : Item
                                PRICE:
                                    type : number
                                    description: Checkout item's price. Default value is the one given in 'AMOUNT'. This parameter is required if you fill any other item parameter.
                                QUANTITY:
                                    type : integer
                                    description: Checkout item's quantity. Default value is 1. This parameter is not required at any situation.
                                    default : 1
                                IMAGE:
                                    type : string 
                                    description: Checkout item's image URL. It must be from your domain. This parameter is not required at any situation.
                                URL:
                                    type : string
                                    description: Checkout item's URL to your domain. This parameter is not required at any situation.
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
    param = request.json
    keys = param.keys()
    expected_keys = ['AMOUNT', 'RETURN_URL', 'CANCEL_URL', 'MERCHANT', 'CURRENCY', 'ITEMS']

    # Checking for required parameters
    if not param or not check_keys(expected_keys[:-2], keys):
        return jsonify({'ERROR': error_message('invalid_request')}), 400

    # Cheking if URI are valid
    if not uri_validator(param['RETURN_URL']) or not uri_validator(param['CANCEL_URL']):
        return jsonify({'ERROR': error_message('invalid_url')}), 400

    # Checking if amount is a valid number
    if not is_number(param['AMOUNT']):
        return jsonify({'ERROR': error_message('invalid_amount')}), 400

    # Checking if merchant exists
    if not db.exists('MERCHANT', 'id', param['MERCHANT']):
        return jsonify({'ERROR': error_message('invalid_merchant')}), 400

    # If request is POST a.k.a creating a new checkout
    if request.method == 'POST':
        while True:
            token = secrets.token_urlsafe(16)
            if not db.exists('CHECKOUT', 'id', token):
                break
    # Else updating existing one
    else:
        if(delete_checkout()[1] == 200):
            token = request.args['checkout_token']
        else:
            return jsonify({'ERROR': error_message('invalid_checkout')}), 400

    # Sorting keys according to db insertion order
    sorted(keys, key=lambda x: expected_keys.index(x))

    # Checking for optional parameters
    if not 'CURRENCY' in keys:
        param['CURRENCY'] = None

    # Inserting new checkout to database
    try:
        db.insert('CHECKOUT', \
            ('id', 'amount', 'return_url', 'cancel_url', 'merchant', 'currency'), \
            tuple( [token] + [param[k] for k in expected_keys[:-1]] ) )
    except Exception as e:
        print(e)
        return jsonify({'ERROR': error_message('db_error')}), 500

    # Adding items to checkout if given by the merchant
    if 'ITEMS' in keys and not add_items(param['ITEMS'], token, param['AMOUNT']):
        delete_checkout()
        return jsonify({'ERROR': error_message('add_items')}), 400

    # Everything went well, returning token for new checkout or true if it was an update
    return (jsonify({'CHECKOUT_TOKEN': token}), 201) if request.method == 'POST' else (jsonify({'SUCCESS': True}), 200)

@app.route('/pay', methods=['GET'])
def pay():
    '''
        Payment page, the client comes to this page after clicking "Pay" on the merchant page
    '''

    # request.args looks ugly and takes too much space...
    args = request.args
    keys = args.keys()
    required_keys = ['checkout_token']

    # Checking for required arguments
    if not args or not check_keys(required_keys, keys):
        return redirect(url_for('index', error = "invalid_checkout"))

    # Getting row from database of the checkout
    checkout = db.get('CHECKOUT', 'id', args['checkout_token']);
    items = db.get_all('ITEM', 'checkout', args['checkout_token']);

    # Checking if checkout is valid
    if not checkout:
        return redirect(url_for('index', error = "invalid_checkout"))

    # Checking if checkout was already paid
    if checkout['status'] != "CREATED":
        return redirect(checkout['return_url'] + "?checkout_token=" + args['checkout_token'] )

    # Checking if user is already logged in
    login_form = False if 'user_id' in session and db.exists('CLIENT', 'id', session['user_id']) else True

    # Checking if there is error message to be shown
    error = False if not request.args.get('error') else request.args.get('error')

    return render_template('pay.html', amount = "{:.2f}".format(checkout['amount']),
                                       items = items,
                                       currency = checkout['currency'] if  checkout['currency'] else 'EUR',
                                       login_form = login_form,
                                       error = error_message(error) ), 200

@app.route('/proccess_payment', methods=['POST'])
def proccess_payment():
    '''
        After the client fills all the information the payment is proccessed
    '''

    # request.args looks ugly and takes too much space...
    args = request.args
    keys = args.keys()
    required_keys = ['checkout']

    # Checking for required arguments
    if not args or not check_keys(required_keys, keys):
        return redirect(url_for('index', error = "invalid_checkout"))

    # Making sure user is logged in
    if not 'user_id' in session or not db.exists('CLIENT', 'id', session['user_id']):
        return redirect(url_for('pay', error = "not_logged", checkout = args['checkout']))


    # Checking if checkout is valid
    if not db.exists('CHECKOUT', 'id', args['checkout']):
        return redirect(url_for('index', error = "invalid_checkout"))

    param = request.form.to_dict()
    keys = param.keys()
    required_keys = ['card-number', 'exp', 'cvc', 'card-owner', 'first_name', \
                    'last_name', 'country', 'city', 'address', 'post_code', 'phone']

    # Checking for required parameters
    if not param or not check_keys(required_keys, keys):
        return redirect(url_for('pay', checkout_token = args['checkout'], error = "invalid_request"))

    # Create a relation of the credit card with the user
    if not add_credit_to_user(param, session['user_id']):
        return redirect(url_for('pay', checkout_token = args['checkout'], error = "db_error" ))

    # Create a relation of the billing address with the user
    billing_id = add_address_to_user(param, session['user_id'])
    if not billing_id:
        return redirect(url_for('pay', checkout_token = args['checkout'], error = "db_error" ))

    # Save information about payment in the checkout
    return_url = prepare_checkout(args['checkout'], param['card-number'], billing_id, session['user_id'])

    if not return_url:
        return redirect(url_for('pay', checkout_token = args['checkout'], error = "db_error"))

    # Redirect to the URL given by the merchant
    return redirect(return_url + "?checkout_token=" + args['checkout'] )

### Generating openapi json file for swagger
with app.test_request_context():
    spec.add_path(view=create_checkout)
    spec.add_path(view=get_checkout)
    spec.add_path(view=execute_checkout)
    spec.add_path(view=delete_checkout)

with open('static/swagger.json', 'w') as f:
    json.dump(spec.to_dict(), f)
###

if __name__ == '__main__':
    app.run(debug=True, port=5000) #run app in debug mode on port 5000
