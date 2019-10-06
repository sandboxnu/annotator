#!/usr/bin/python

import numpy as np
import pandas as pd
from os import listdir
from pandas import read_csv
import math 

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

def get_activity_accelerometer_data(activities, time_range, folder):
    """
        sklearn_input: [Activities], TimeInterval -> [Activities], [labels], [feature_names], [features]

        Queries data and converts it to a format palatable for sklearn.
    """

    data = get_data(folder).values
    features = np.zeros(shape=(1, 3))
    labels = np.zeros(shape=(1, 1))
    feature_names = ['x', 'y', 'z']

    for row in data:
        if(row[4] in activities) and ((time_range[0] <= row[0]) and (row[0] <= time_range[1])):
            features = np.vstack([features, row[1:4]])
            labels = np.append(labels, row[4])

    return activities, labels, feature_names, features

## DEPRECATED
def data_subset(activities, time_range, folder):
    """"a, b, c, d, e ... z,"

        data_subset: Activity, (TimeStamp, TimeStamp), FolderPath -> [[TODO:]]

        Given an activity, time range and path to the folder data,
        produces an nested array with all of the relevant data given
        the parameters passed.
    """
    data = get_data(folder)
    processed_arr = np.zeros(shape=(1, 5))
    for row in data:
        if (row[0] in activities) and ((time_range[0] <= row[1]) and (row[1] <= time_range[1])):
            processed_arr = np.vstack([processed_arr,row])
    # inneficient: has to reinstantiate array every time
    # list could be much faster
    # .tolist())
    print(activities)
    print(processed_arr)
    print(time_range)
    # return activities;

def get_data(folder):
    """
        get_data: Folder -> [[TimeStamp, X, Y, Z, Activity]]

        Given the file path to the folder containing our data.
        produces an array of arrays with the data for ease of organization.

        Assumes that the folder path given contains csv data with rows
        of the form specified in the return type of the function signature.
    """
    data = pd.DataFrame()
    for name in listdir(folder):
        filename = folder + '/' + name
        if filename.endswith('.csv'):
            df = read_csv(filename, header=None)
            data = data.append(df)

    return data

def get_xyz_data(folder):
    data = get_data(folder)
    data.drop(data.columns[[0]], axis = 1, inplace=True)
    data.columns = ['x', 'y', 'z', 'label']
    return data

def get_naive_ts_data(folder, num_bins):
    data = get_data(folder)

    # prepare labels
    labels = []
    for i in range(num_bins * 3):
        mod3 = i % 3
        labelInd = str(math.floor(i / 3))
        if(mod3 == 0):
            labels.append('x' + labelInd)
        elif(mod3 == 1):
            labels.append('y' + labelInd)
        else:
            labels.append('z' + labelInd)
    labels.append('label')

    dataTs = pd.DataFrame(columns = labels)

    curLabel = 1
    curData = []
    countThrown = 0
    countLoops = 0

    for row in data.values:
        countLoops += 1
        # print(curData)
        if len(curData) == num_bins*3:
            # add label to end of data
            curData.append(curLabel)
            arr = pd.DataFrame(columns = labels, data = [curData])
            # append this data to df 
            dataTs = pd.concat([dataTs, arr])

            # reset with the new row
            curLabel = row[4]
            curData = []
            for val in row[1:4]:
                curData.append(val)

        elif row[4] != curLabel:
            countThrown += 1

            # discard old data: not big enough sample
            # reset the data with new data
            curLabel = row[4]
            curData = []
            for val in row[1:4]:
                curData.append(val)
        else:
            # if no special conditions, append to array
            # and increment the number of rows
            for val in row[1:4]:
                curData.append(val)
    
    return dataTs

# Test script

print(get_naive_ts_data("./data", 20))
# label_names, labels, feature_names, features = get_activity_accelerometer_data([1, 2, 3, 4, 5, 6, 7], (0, 100000), "./data")

# print(label_names)
# print(labels)
# print(feature_names)
# print(features)
#data_subset([1], (0, 2000), "./data")
# np.savetxt("data{0}-{1}.csv".format(activities, time_range).replace(" ", ""), processed_arr.transpose(), '%16.2f', delimiter=",")
