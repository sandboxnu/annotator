Classifier Experiment

The goal of this experiment is to build a module that can load in a sequence of accelerometer data (x, y, z) and output a predicted activity label. The goal is to link this module to a Flask REST API that will let our app make activity predcition requests.

TODO
1. Function to load raw data and transform into wide dataset that we can train on
2. Experiment with different downsampling and window sizes (save test results)
3. Test on algorithms like support vector machines as a baseline
4. Test on a convolutional neural network (CNN) 
5. Can we make the algorithm online, i.e. it learns when each new data point is sent in?


