echo "scraping ips"
ack -h '^\d{1,3}\.\d{1,3}\..*' ../build/firehof > ../build/ip_ack.txt
echo "sorting ips"
sort ../build/ip_ack.txt > ../build/ip_sorted.txt
echo "deduping ips"
uniq ../build/ip_sorted.txt > ../build/ips.txt

