import sys
import datetime

print(f"Hello from Python! Version: {sys.version}")
print(f"Current time: {datetime.datetime.now()}")

try:
    import pandas as pd
    print(f"Pandas version: {pd.__version__}")
except ImportError:
    print("Pandas is not installed.")

try:
    import numpy as np
    print(f"NumPy version: {np.__version__}")
except ImportError:
    print("NumPy is not installed.")
