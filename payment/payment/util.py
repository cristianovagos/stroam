from urllib.parse import urlparse

def check_parameters(parameters, request):
    '''
        Checks if every required parameter is on the request
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
