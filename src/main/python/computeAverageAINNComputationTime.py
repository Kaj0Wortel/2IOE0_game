# Python script
log_file = r'C:\Users\s165839\Documents\gitStuff\2IOE0_game\src\main\java\src\log.log'

sum = 0
number_of_lines = 0

with open(log_file, 'r+') as file:
    for line in file:
        if "it took" in line:
            number_of_lines += 1
            sum += int(line[-7:-4]) # Assumption: amount of ms is 3 digits long
    file.write("Average computation-time of AINN: " + str(sum/number_of_lines))
# Uncomment to print to console
# print(str(sum/number_of_lines))