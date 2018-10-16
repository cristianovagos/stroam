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
