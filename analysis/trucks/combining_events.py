import xml.etree.ElementTree as ET
import gzip

# Load the events file using a raw string for the path
file_path = r"C:\Users\djarvis3\Box\MATSim_Analysis\Truck_Analysis\Truck Events\Seed 472\combined_trucks.events.xml.gz"
tree = ET.parse(gzip.open(file_path, 'rt', encoding='utf-8'))
root = tree.getroot()

# Dictionary to store the last event time and link for each vehicle
last_event = {}

# Loop through the events to identify the last link and time for each vehicle
for event in root:
    if 'vehicle' in event.attrib:
        vehicle_id = event.attrib['vehicle']
        time = float(event.attrib['time'])
        if 'link' in event.attrib:
            link_id = event.attrib['link']
            if vehicle_id not in last_event or time > last_event[vehicle_id][0]:
                last_event[vehicle_id] = (time, link_id)

# Create dummy events for each vehicle on its last link at the 24th hour (86400 seconds)
for vehicle_id, (time, link_id) in last_event.items():
    dummy_event = ET.SubElement(root, 'event', {
        'time': "86400.0",
        'type': "vehicle remains",
        'vehicle': vehicle_id,
        'link': link_id
    })

# Save the modified events to a new file
output_path = r"C:\Users\djarvis3\Box\MATSim_Analysis\Truck_Analysis\Truck Events\Seed 472\3-21-472_trucks_modified_with_dummy.events.xml.gz"

# Fix for the write() argument error
with gzip.open(output_path, 'wt', encoding='utf-8') as f_out:
    f_out.write('<?xml version="1.0" encoding="UTF-8"?>\n')
    f_out.write(ET.tostring(root, encoding="utf-8").decode("utf-8"))

print("Dummy events added and saved to:", output_path)
