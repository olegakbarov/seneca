# Nexus

A frontend webapp.

- [ ] webserver ring passes all routes to client

- [ ] clientside routing with bidi

- [ ] css & grid system

- [ ] state management with re-frame


### Handy snippets

```
 (ns nexus.app
   (:require
       [nexus.helpers.core :refer [log]]
       [reagent.core :as r :refer [atom]]
       [reagent.session :as session]
       [goog.events :as events]
       [goog.history.EventType :as EventType]
       [nexus.routes :refer [current-page]])
   (:require-macros [cljs.core.async.macros :refer (go)])
   (:import goog.history.Html5History
            goog.Uri))
```
