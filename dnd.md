
## cljs-dnd

Should use cljs strengths: `core.async` and `google closure`

##  Docs

Under the hood, all the backends do is translate the DOM events into the internal Flux
actions that React DnD can process.

Types are useful because, as your app grows, you might want to make more things draggable, but you don't necessarily want all the existing drop targets to suddenly start reacting to the new items.

The connectors let you assign one of the predefined roles (a drag source, a drag preview, or a drop target) to the DOM nodes in your render function.

###  DragDropContext

```js
export default function DragDropContext(backendOrModule)
  ...
  const childContext = {
    dragDropManager: new DragDropManager(backend)
  };

  ..

  class DragDropContextContainer extends Component {
    getChildContext() {
      return childContext;
    }
  }

  ...

  return hoistStatics(DragDropContextContainer, DecoratedComponent);
}
```


### HTML5Backend

```js
export default class HTML5Backend {
  ...
  this.getSourceClientOffset
  this.handleTopDragStart
  this.handleTopDragStartCapture
  this.handleTopDragEndCapture
  this.handleTopDragEnter
  this.handleTopDragEnterCapture
  this.handleTopDragLeaveCapture
  this.handleTopDragOver
  this.handleTopDragOverCapture
  this.handleTopDrop
  this.handleTopDropCapture
  this.handleSelectStart
  this.endDragIfSourceWasRemovedFromDOM
  this.endDragNativeItem
}
```

```js
addEventListeners(target) {
  target.addEventListener('dragstart', this.handleTopDragStart);
  target.addEventListener('dragstart', this.handleTopDragStartCapture, true);
  target.addEventListener('dragend', this.handleTopDragEndCapture, true);
  target.addEventListener('dragenter', this.handleTopDragEnter);
  target.addEventListener('dragenter', this.handleTopDragEnterCapture, true);
  target.addEventListener('dragleave', this.handleTopDragLeaveCapture, true);
  target.addEventListener('dragover', this.handleTopDragOver);
  target.addEventListener('dragover', this.handleTopDragOverCapture, true);
  target.addEventListener('drop', this.handleTopDrop);
  target.addEventListener('drop', this.handleTopDropCapture, true);
}
```

```js
connectDragSource(sourceId, node, options) {
  this.sourceNodes[sourceId] = node;
  this.sourceNodeOptions[sourceId] = options;

  const handleDragStart = (e) => this.handleDragStart(e, sourceId);
  const handleSelectStart = (e) => this.handleSelectStart(e, sourceId);

  node.setAttribute('draggable', true);
  node.addEventListener('dragstart', handleDragStart);
  node.addEventListener('selectstart', handleSelectStart);

  return () => {
    delete this.sourceNodes[sourceId];
    delete this.sourceNodeOptions[sourceId];

    node.removeEventListener('dragstart', handleDragStart);
    node.removeEventListener('selectstart', handleSelectStart);
    node.setAttribute('draggable', false);
  };
}

connectDropTarget(targetId, node) {
  const handleDragEnter = (e) => this.handleDragEnter(e, targetId);
  const handleDragOver = (e) => this.handleDragOver(e, targetId);
  const handleDrop = (e) => this.handleDrop(e, targetId);

  node.addEventListener('dragenter', handleDragEnter);
  node.addEventListener('dragover', handleDragOver);
  node.addEventListener('drop', handleDrop);

  return () => {
    node.removeEventListener('dragenter', handleDragEnter);
    node.removeEventListener('dragover', handleDragOver);
    node.removeEventListener('drop', handleDrop);
  };
}
```
