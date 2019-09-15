#!/usr/bin/python

import numpy as np
from os import listdir
from pandas import read_csv

'''
An Activity is one of:
--- 1: Working at Computer
--- 2: Standing Up, Walking and Going updown stairs
--- 3: Standing
--- 4: Walking
--- 5: Going UpDown Stairs
--- 6: Walking and Talking with Someone
--- 7: Talking while Standing
And represents an activity performed by a user with a convenient label.

We define a unit of time as a positive integer - this will be replaced
with reasonable timestamps as we transition from using the sample data
from collecting our own data with real timestamps. For now we will assume that
the time between each data point is consistent.
'''

'''
    data_subset: Activity, (TimeStamp, TimeStamp), FolderPath -> [[TODO:]]

    Given an activity, time range and path to the folder data,
    produces an nested array with all of the relevant data given
    the parameters passed.
'''
def data_subset(activity, time_range, folder):
    data = get_data(folder)
    
    arr = np.array([[]])
    for datapoint in data:
        if datapoint[4] == activity and time_range[0] <= datapoint[0] <= time_range[1]:
            # append to arary

    arr.transpose()


'''
    get_data: Folder -> [[TimeStamp, X, Y, Z, Activity]]

    Given the file path to the folder containing our data.
    produces an array of arrays with the data for ease of organization.
'''
def get_data(folder):
    data = list()
    for name in listdir(folder):
        filename = folder + '/' + name
        if filename.endswith('.csv'):
            df = read_csv(filename, header=None)
            # TODO: do we want this?
            data.append(df)
    
    return data 



data_subset(1, (0, 2), "./data/1.csv")
