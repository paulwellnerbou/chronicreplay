#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Aug  6 10:24:07 2020

@author: TeSolva
"""

import numpy as np

import pandas as pd

import matplotlib.dates as mdates

import matplotlib.pyplot as plt

import os

import re


#%%

def my_int(my_string):

    try:

        out = int(my_string)

    except:

        out = -9999

    

    return out


#%%
def func_nummer(x):

    temp = re.findall(r"[\w']+", x)

    my_list=list(map(my_int, temp))
    
    temp = np.array(my_list)[np.array(my_list)>-9999]

    if len(temp) > 1:

        out = temp[0]

    else:

        out = False

    return out
#%%

def func_id(x):

    temp = re.findall(r"[\w']+", x)

    my_list=list(map(my_int, temp))
    
    temp = np.array(my_list)[np.array(my_list)>-9999]

    if len(temp) > 1:

        out = temp[1]

    else:

        out = False

    return out
#%%

def outlier_1d_mad_based(sample, thresh=3.5):

    """

    outlier_1d_mad_based(sample, thresh=3.5)

    routine to analyse a given 1d data sample to check for outliers.

    see reference for more details on the background of the used algorithm.



    the function returns a boolean array with True if a value in the sample

    is an outliers and False otherwise.



    Parameters:

    -----------

        sample : array_like

            An numobservations by numdimensions array of observations



        thresh : float

            The modified z-score to use as a threshold. Observations with

            a modified z-score (based on the median absolute deviation) greater

            than this value will be classified as outliers.



    Returns:

    --------

        A numobservations-length boolean array.



    Examples

    --------

        # Generate some data

        sample = np.random.normal(0, 0.5, 50)



        # Add three outliers...

        sample = np.r_[sample, -3, -10, 12]



        # call function and check for outliers

        out = outlier_1d_mad_based(sample)



    References:

    ----------

        Boris Iglewicz and David Hoaglin (1993), "Volume 16: How to Detect and

        Handle Outliers", The ASQC Basic References in Quality Control:

        Statistical Techniques, Edward F. Mykytka, Ph.D., Editor.

    """

    if len(sample.shape) == 1:

        sample = sample[:, None]



    median = np.median(sample, axis=0)

    diff = np.sum((sample - median) ** 2, axis=-1)

    diff = np.sqrt(diff)

    med_abs_deviation = np.median(diff)



    modified_z_score = 0.6745 * diff / med_abs_deviation



    return modified_z_score > thresh

#%%

# #path = "PATH\"

# # define all filenames in current directory

# path = os.getcwd() # path to this file here

# list_dir = os.listdir(os.getcwd()) # all filenames in the directory

# file_set = []

# for i in list_dir:

#     if i.endswith(".csv"):

#         file_set.append(i)
#%%


# define file name

file1 = "chronicreplay-audiothek-appapi.tesolva.dev_2020-08-08_07-29-08.csv"

file2 = "chronicreplay-audiothek-appapi.solr.tesolva.dev_2020-08-11_08-47-32.csv"

#

file_set = [file1, file2]


###############################################################################

#%% pre-process
X_file = file_set[1]
filename = os.path.splitext(X_file)[0]

path = os.getcwd()
outputPath = path + '/' + filename + '/' + filename
os.makedirs(filename, exist_ok=True)


df = pd.read_csv(X_file,sep='\t')


df.StartTime = pd.to_datetime(df.StartTime)


# create new columns

df['Request_No']=df.Request    

df['Request_ID']=df.Request

# add values (No and ID) from URL to new columns 

df.Request_No = df.Request_No.apply(func_nummer)

df.Request_ID = df.Request_ID.apply(func_id)

# Requests without /health and /metric

df = df[df['Request_No'] != False]


#%%
# Threshold to find URLs with high values of request time

THLD = 1000 # Threshold

df['Duration_greaterThan_1000'] = (df.Duration>THLD).astype(int)

#%%
plt.figure(figsize = (8.5,11))
myFmt = mdates.DateFormatter('%H:%M')
plt.gca().xaxis.set_major_formatter(myFmt)

plt.scatter(df.StartTime[df.Duration<THLD].values, df.Duration[df.Duration<THLD].values, marker='^', label='Duration < THLD [ms]',

               alpha=0.3, edgecolors='none')

plt.scatter(df.StartTime[df.Duration>THLD].values, df.Duration[df.Duration>THLD].values, marker='o', label='Duration > THLD [ms]',

               alpha=0.3, edgecolors='none')

# Show the boundary between the regions:
plt.plot(df.StartTime.values, np.ones(len(df))*THLD,'-.k')

plt.legend()
plt.ylabel("Duration of request time (ms)")
plt.xlabel("Time")
plt.title("file:" + X_file)
plt.axis([None, None, 0, THLD*2])

plt.savefig(outputPath + '-Duration.png')

#%%
plt.figure(figsize = (8.5,11))
myFmt = mdates.DateFormatter('%H:%M')
plt.gca().xaxis.set_major_formatter(myFmt)

plt.scatter(df.StartTime[df.Duration<THLD].values, df.Difference[df.Duration<THLD].values, marker='^', label='Duration < THLD [ms]',

               alpha=0.3, edgecolors='none')

plt.scatter(df.StartTime[df.Duration>THLD].values, df.Difference[df.Duration>THLD].values, marker='o', label='Duration > THLD [ms]',

               alpha=0.3, edgecolors='none')

# Show the boundary between the regions:
plt.plot(df.StartTime.values, np.ones(len(df))*THLD,'-.k')

plt.legend()
plt.ylabel("Difference of request time (ms)")
plt.xlabel("Time")
plt.title("file:" + X_file)
plt.axis([None, None, 0, THLD*2])

plt.savefig(outputPath + '-Difference.png')

#%%
# only interested in URLs with request time greater than THLD

df_Upper = df[df.Duration>THLD]

df_Upper['Percent'] = len(df_Upper)/len(df.index)*100    
df_Upper['Mean'] = df_Upper["Duration"].mean()

# export csv files
df_Upper[['Duration', 'Request_No', 'Request_ID', 'Percent','Mean', 'Request']].to_csv(outputPath + '-URLs-request-time-Upper.csv', index=False)


#%%
# count Request_No for Requests time > THLD

df_temp = (df_Upper.drop_duplicates().Request_No.value_counts())

df_Counts = pd.DataFrame({'Request_No':df_temp.index, 'Counts':df_temp.values})

df_Counts.to_csv(outputPath + '-RequestsNo-Counts-Upper.csv', index=False)

#%%
# values between 500 and 1000 ms

df_Between = df[df['Duration'].between(500, 1000, inclusive=True)]

# export csv files

df_Between['Percent'] = len(df_Between)/len(df.index)*100

df_Between['Mean'] = df_Between["Duration"].mean()

df_Between[['Duration', 'Request_No', 'Request_ID', 'Percent','Mean', 'Request']].to_csv(outputPath + '-URLs-request-time-Between.csv', index=False)

# count Request_No for Requests time  500 < time < 1000

df_temp2 = (df_Between.drop_duplicates().Request_No.value_counts())

df_CountsBetween = pd.DataFrame({'RequestNo':df_temp2.index, 'Counts':df_temp2.values})

df_CountsBetween.to_csv(outputPath + '-RequestsNo-Counts-Between.csv', index=False)


#%%
# values lower than 500 ms
df_Lower = df[df['Duration'].between(0, 500, inclusive=False)]

# export csv files

df_Lower['Percent'] = len(df_Lower)/len(df.index)*100

df_Lower['Mean'] = df_Lower["Duration"].mean()

df_Lower[['Duration', 'Request_No', 'Request_ID', 'Percent','Mean', 'Request']].to_csv(outputPath + '-URLs-request-time-Lower.csv', index=False)

# count Request_No for Requests time  < 500

df_temp2 = (df_Lower.drop_duplicates().Request_No.value_counts())

df_CountsLower = pd.DataFrame({'RequestNo':df_temp2.index, 'Counts':df_temp2.values})

df_CountsLower.to_csv(outputPath + '-RequestsNo-Counts-Lower.csv', index=False)


#%%

# df_notSameStatus = df[df.SameStatus==False]


# # export csv files
# df_notSameStatus.to_csv(outputPath + '-URLs-wo-same-status.csv', index=False)

#%%
del(X_file,file1, file2, file_set) # delete the variable from workspace 
#%%

