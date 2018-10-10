from urllib.parse import urlparse

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
