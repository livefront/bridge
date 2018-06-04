Change Log
==========

Version 1.1.2 *(2018-06-04)*
----------------------------

 * Data will no longer be automatically cleared for objects that go out of memory while an app is running. It is now more strongly recommended to use the `clear` method to manually delete data for objects in the process of being discarded.

Version 1.1.1 *(2017-08-17)*
----------------------------

 * Fixed `clear` behavior for a removed `Fragment` in the backstack.

Version 1.1.0 *(2017-08-16)*
----------------------------

 * Added support for saving Bitmaps.
 * Improved automatic clearing of old data.
 * Added `clear` method for manually clearing data from disk for a given object.
 * Added `clearAll` method for manually clearing all data from disk associated with `Bridge`.

Version 1.0.0 *(2017-06-22)*
----------------------------

Initial release.