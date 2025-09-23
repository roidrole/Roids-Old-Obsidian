Notice : use keywords **added :**, **changed :**, **removed :** if > 2 entries. 
See [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)

## 2.0.0 :
**Added:**
- Hot reloading of json conversions

**Changed:**
- Full rewrite
- Optimization (≈ 5 times faster)
- OredictMatcher now matches based on picked blocked (middle mouse button) instead of registry names
- Specifically listens to NeighbourNotifyEvent fired by BlockLiquid (so no placing blocks between liquids)

**Removed:**
- checkDustPower argument. Entries with it specified won't error, but it will get ignored. Use the regex matcher instead

## 1.0.1 : 
**Fixed** a breaking bug that didn't allow things to work when checking dusts.

## 1.0.0 :
First release