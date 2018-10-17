from urllib.parse import urlparse
import database as db

def check_keys(required_keys, keys):
    '''
        Checks if every required parameter is on the request
    '''
    for key in required_keys:
        if key not in keys:
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

def error_message(error):
    '''
        Dict that translate error messages
    '''

    errors = {
        "wrong_pass" : "Wrong e-mail or password, please try again.",
        "login_fails" : "Login failed, please try again...",
        "invalid_checkout" : "Checkout not valid, please contact the responsible merchant.",
        "invalid_request" : "Invalid format or parameters missing.",
        "invalid_url" : "RETURN or CANCEL URI with invalid format.",
        "invalid_amount" : "AMOUNT not valid.",
        "invalid_merchant" : "MERCHANT doesn\'t exist.",
        "db_error" : "An error ocurred on the Database.",
        "not_logged" : "You must be logged in for this operation."

    }

    return errors.get(error)

def add_items(items, checkout):
    '''
        Add items to checkout
    '''
    for item in items:
        keys_to_db = ['NAME', 'PRICE']
        columns = ('checkout', 'name', 'price')
        keys = item.keys()
        if not check_keys(keys_to_db, keys):
            return False
        if 'QUANTITY' in keys:
            keys_to_db += 'QUANTITY'
            columns = (*columns, 'quantity')
        if 'URL' in keys:
            keys_to_db += 'URL'
            columns = (*columns, 'url')
        try:
            db.insert('ITEM', \
                columns, \
                tuple( [checkout] + [item[k] for k in keys_to_db] ) )
        except Exception as e:
            print(e)
            return False
    return True

def add_credit_to_user(credit_card, user_id):
    '''
        Adds credit card to list of cards owner by the user
    '''

    if not db.exists('CREDIT_CARD', ['user_id', 'cc_number'], [user_id, credit_card['card-number']]):
        try:
            db.insert('CREDIT_CARD', ('cc_number', 'csv', 'expiration', 'owner_name', 'user_id'),
                       (credit_card['card-number'], credit_card['cvc'], credit_card['exp'], credit_card['card-owner'], user_id))
        except Exception as e:
            print(e)
            return False

    return True

def prepare_checkout(checkout_number, card_number, user_id):
    '''
        Prepare checkout by adding the buyer and credit card to be used
    '''

    # Getting row from database of the checkout
    checkout = db.get('CHECKOUT', 'id', checkout_number)

    if checkout['status'] != 'CREATED':
        return False

    try:
        db.update('CHECKOUT', ('paid_with', 'paid_by', 'status'),
                                    (card_number, user_id, 'READY'),
                                    'id', checkout_number)
    except Exception as e:
        print(e)
        return False

    return checkout['RETURN_URL']
