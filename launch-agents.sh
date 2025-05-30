#!/bin/bash

for i in {1..3}; do
  open -na "Brave Browser Nightly" --args --user-data-dir="/tmp/brave-profile-player-$i" --no-first-run --new-window "http://localhost:3000"
done
