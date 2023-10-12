import gzip
import xml.etree.ElementTree as ET

input_file = "C:/Users/djarvis3/Box/MATSim_Analysis/Truck_Analysis/Truck Events/Seed 472/3-21-472_trucks.events.xml.gz"
output_file = "C:/Users/djarvis3/Box/MATSim_Analysis/Truck_Analysis/Truck Events/Seed 472/3-21-472_trucks_modified.events.xml.gz"

with gzip.open(input_file, 'rt', encoding="utf-8") as f_in:
    tree = ET.parse(f_in)
    root = tree.getroot()

    # Loop through all 'event' tags
    for event in root.findall('event'):
        # If 'vehicle' attribute exists, modify its value
        if 'vehicle' in event.attrib:
            event.attrib['vehicle'] += "_INCREASED"

    # Convert the modified XML tree to a string
    xml_string = ET.tostring(root, encoding="utf-8").decode("utf-8")

    # Write the modified XML string to a gzip file
    with gzip.open(output_file, 'wt', encoding="utf-8") as f_out:
        f_out.write(xml_string)
