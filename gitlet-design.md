# Gitlet Design Document

**Name**: Abhiram Gudimella, Alexander Tian

## Classes and Data Structures
## Repository
#### Attributes

1.

### Commit

#### Attributes

1. Message - contains the message of commit
2. Timestamp - time at which the commit was constructed
3. Parent - the parent of a commit object (which is also a commit)

### Class 2

#### Attributes

1. Field 1
2. Field 2

### Class 2

#### Attributes

1. Field 1
2. Field 2

### Class 2

#### Attributes

1. Field 1
2. Field 2

### Class 2

#### Attributes

1. Field 1
2. Field 2

# Structure
### 1 .gitlet
### 2 gitletRepo (dir mkdir - created with init)
### 3 Branches* (dir mkdir - created with init)
### 3 Commits (dir mkdir - created with init)
### 4 commits (hash map file of all commits serialized as object writeObject created with commit)
### 3 Blobs (dir - created with init)
### 4 blob1 (object stored as byte array - writecontents - created with commit)
### 4 blob2
### 4 ...
### 3 Staging Area (dir - created with init)
### 4 Staged for Addition (dir - created with init)
### 5 (Files to be added - created with add and removed with commit)
### 4 Staged for Removal (dir - created with init)
### 5 (Files to be removed - created with add and removed with commit)
### 

## Algorithms

## Persistence

