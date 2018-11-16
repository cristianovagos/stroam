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
        "not_logged" : "You must be logged in for this operation.",
        "checkout_not_ready" : "Checkout wasn't set ready by the buyer yet.",
        "add_items" : "Error adding the items, please check their format",
        "no_login" : "You must be logged in to perform this action.",
        "register_fails" : "Registration failed, please try again...",
        "user_exists" : "E-mail is already registered."
    }

    return errors.get(error)

def add_items(items, checkout, amount):
    '''
        Add items to checkout
    '''

    # Checking if total amount matches the sum of all items
    total = 0
    for item in items:
        total += item['PRICE']
    if total != amount:
        return False

    # Adding items to database
    for item in items:
        keys_to_db = ['NAME', 'PRICE']
        columns = ('checkout', 'name', 'price')
        keys = item.keys()
        if not check_keys(keys_to_db, keys):
            return False
        if 'QUANTITY' in keys:
            keys_to_db.append('QUANTITY')
            columns = (*columns, 'quantity')
        if 'URL' in keys:
            keys_to_db.append('URL')
            columns = (*columns, 'url')
        if 'IMAGE' in keys:
            keys_to_db.append('IMAGE')
            columns = (*columns, 'image')
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
    # If credit_card is already related to user ignore
    if not db.exists('CREDIT_CARD', ['user_id', 'cc_number'], [user_id, credit_card['card-number']]):
        try:
            db.insert('CREDIT_CARD', ['cc_number', 'csv', 'expiration', 'owner_name', 'user_id'],
                       [credit_card['card-number'], credit_card['cvc'], credit_card['exp'], credit_card['card-owner'], user_id])
        except Exception as e:
            print(e)
            return False

    return True

def add_address_to_user(address, user_id):
    '''
        Adds billing address to user
    '''

    # Checking if such BILLING_ADDRESS already exists
    if not db.exists('BILLING_ADDRESS', \
    ['first_name', 'last_name', 'country', 'city', 'address', 'post_code', 'phone', 'user_id'],\
    [address['first_name'], address['last_name'], address['country'], \
    address['city'], address['address'], address['post_code'], address['phone'], user_id]):
            try:
                return db.insert('BILLING_ADDRESS',\
                 ['first_name', 'last_name', 'country', 'city', 'address', 'post_code', 'phone', 'user_id'], \
                 [address['first_name'], address['last_name'], address['country'], \
                 address['city'], address['address'], address['post_code'], address['phone'], user_id])
            except Exception as e:
                print(e)
                return False

    return True

def prepare_checkout(checkout_number, card_number, billing_id, user_id):
    '''
        Prepare checkout by adding the buyer and credit card to be used
    '''

    # Getting row from database of the checkout
    checkout = db.get('CHECKOUT', 'id', checkout_number)

    if checkout['status'] != 'CREATED':
        return False

    try:
        db.update('CHECKOUT', ['paid_with', 'paid_by', 'status', 'billing_address'],
                                    [card_number, user_id, 'READY', billing_id],
                                    'id', checkout_number)
    except Exception as e:
        print(e)
        return False

    return checkout['RETURN_URL']
