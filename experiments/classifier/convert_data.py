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


def data_subset(activities, time_range, folder):
    """
        data_subset: Activity, (TimeStamp, TimeStamp), FolderPath -> [[TODO:]]

        Given an activity, time range and path to the folder data,
        produces an nested array with all of the relevant data given
        the parameters passed.
    """
    data = get_data(folder)

    processed_arr = list()
    for datapoint in data:
        if (datapoint[4] in activities) & ((time_range[0] <= datapoint[0]) & (datapoint[0] <= time_range[1])):
            # append to array
            processed_arr.append(datapoint[1,4])

    arr = np.array(processed_arr)
    arr.transpose()
    print(arr)



def get_data(folder):
    """
        get_data: Folder -> [[TimeStamp, X, Y, Z, Activity]]

        Given the file path to the folder containing our data.
        produces an array of arrays with the data for ease of organization.

        Assumes that the folder path given contains csv data with rows
        of the form specified in the return type of the function signature.
    """
    data = list()
    for name in listdir(folder):
        filename = folder + '/' + name
        if filename.endswith('.csv'):
            df = read_csv(filename, header=None)
            # TODO: do we want this?
            data.append(df)
    
    return data


# Test script
data_subset([1], (0, 2), "./data")