CRUD Strategy Hints
===================

Schema provides hints on how to handle CRUD interactions with the data and the data it relates to.  By themselves, they are just hints, a predefined vocabulary of strategies, and do not actually to anything.  It's up to the implementations that bridge the Schema to datastores to provide actions for those strategies.

It is not the goal of Schema to provide hints for every possible interaction strategy.  Instead Schema strives to provide hints for a common list of strategies, as well as provide a a facility to have custom strategies. 

Schema/Entity Strategies
------------------------

The vocabulary hints for CRUD strategies define how CRUD should be handled for that particulary entity.  The hints can be placed on the schema to establish a global strategy, and on entities to establish an entity specific strategy.  Hints on entities take precedence.

<table>
    <thead>
        <tr>
            <th>CRUD Action</th>
            <th>Schema/Entity Property</th>
            <th>Values | Vocabulary Hints</th>
            <th>Strategy Definitions</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td rowspan="2">CREATE</td>
            <td rowspan="2">:create-strategy</td>
            <td>nil -or- :default</td>
            <td>Create entity using native processes, then perform :on-create events on relations.</td>
        </tr>
        <tr>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td rowspan="4">RETRIEVE</td>
            <td rowspan="4">:retrieve-strategy</td>
            <td>nil -or- :default</td>
            <td>Retrieve entity data, then perform :on-retrieve events on relations.</td>
        </tr>
        <tr>
            <td>:attributes</td>
            <td>Retrieves only the attributes on the entity, and ignores the relations.</td>
        </tr>
        <tr>
            <td>:relations</td>
            <td>Retrieves only the relations on the entity, and ignores the attributes.</td>
        </tr>
        <tr>
            <td>A collection</td>
            <td>A collection of paths and instructions for retrieving data for the entity.  All other instructions/hints are ignored.  See the [Entity Navigation Map - TBD] for specification for details.</td>
        </tr>
        <tr>
            <td rowspan="2">UPDATE</td>
            <td rowspan="2">:update-strategy</td>
            <td>nil -or- :default</td>
            <td>Update entity using native processes, then perform :on-update events on relations.</td>
        </tr>
        <tr>
            <td></td>
            <td></td>
        </tr>
        <tr>
            <td rowspan="3">DELETE</td>
            <td rowspan="3">:delete-strategy</td>
            <td>nil -or- :default</td>
            <td>Delete entity using native processes, then perform :on-delete events on relations.</td>
        </tr>
        <tr>
            <td>:deactivate</td>
            <td>Set a predetermine attribute representing an active state to false, then perform :on-delete events on relations.</td>
        </tr>
        <tr>
            <td>:expire</td>
            <td>Set a predetermine attribute the date/time the entity expires to the current date/time, then perform :on-delete events on relations.</td>
        </tr>
    </body>
</table>

Relation Strategies
-------------------
Relations attached to entities have event strategies hints for responding to CRUD actions on their parent entity.  If a relation has a valid event strategy, that strategy should only be acted upon if the persisted data has a valid field value that represents the relation.  Generally, if the reference data has no values that represent its IDENTITY, it would be considered a candidate for creation.  If it does have IDENTITY values, it is a candidate for update.

<table class="strategy-events">
    <thead>
        <tr>
            <th style="width:5em">CRUD Event</th>
            <th style="width:6em">Relation Property</th>
            <th style="width:9em">Values | Vocabulary Hints</th>
            <th>Strategy Definitions</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td rowspan="4">CREATE</td>
            <td rowspan="4">:on-create</td>
            <td>nil -or- :ignore</td>
            <td>Do nothing.</td>
        </tr>
        <tr>
            <td>:cascade</td>
            <td>If a field exists on the provided data map being persisted that represents the relation the strategy is on, that data should be updated or created accordingly for the target entity.</td>
        </tr>
        <tr>
            <td>:cascade-update</td>
            <td>If a field exists on the provided data map being persisted that represents the relation the strategy is on, and the field data represents data that should be updated, that data will be updated for the target entity.</td>
        </tr>
        <tr>
            <td>:cascade-create</td>
            <td>If a field exists on the provided data map being persisted that represents the relation the strategy is on, and the field data represents data that should be created, that data will be updated for the target entity.</td>
        </tr>
        <tr>
            <td rowspan="2">RETRIEVE</td>
            <td rowspan="2">:on-retrieve</td>
            <td>nil -or- :ignore</td>
            <td>Do nothing.</td>
        </tr>
        <tr>
            <td>A collection</td>
            <td>A collection of paths and instructions for retrieving data through the relationship.  See the [Entity Navigation Map - TBD] for specification for details.</td>
        </tr>
        <tr>
            <td rowspan="4">UPDATE</td>
            <td rowspan="4">:on-update</td>
            <td>nil -or- :ignore</td>
            <td>Do nothing.</td>
        </tr>
        <tr>
            <td>:cascade</td>
            <td>If a field exists on the provided data map being persisted that represents the relation the strategy is on, that data should be updated or created accordingly for the target entity.</td>
        </tr>
        <tr>
            <td>:cascade-update</td>
            <td>If a field exists on the provided data map being persisted that represents the relation the strategy is on, and the field data represents data that should be updated, that data will be updated for the target entity.</td>
        </tr>
        <tr>
            <td>:cascade-create</td>
            <td>If a field exists on the provided data map being persisted that represents the relation the strategy is on, and the field data represents data that should be created, that data will be updated for the target entity.</td>
        </tr>
        <tr>
            <td rowspan="2">DELETE</td>
            <td rowspan="2">:on-delete</td>
            <td>nil -or- :ignore</td>
            <td>Do nothing.</td>
        </tr>
        <tr>
            <td>:cascade</td>
            <td>Delete any entity of :related-by type that is related to the deleted parent entity through the current relationship.</td>
        </tr>
    </body>
</table>
