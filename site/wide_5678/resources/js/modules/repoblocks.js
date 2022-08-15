/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/


function createRepoItem(f, a,compname,isui,mx,my,configobj) {

}


repo_blocks_init = function(){
	 configs["RepoItem"] = createRepoItem;
    comps["RepoItem"] = dummy;
    //
    

        repomodel = new dijit.tree.ForestStoreModel({
            store: repostore,
            query: {
                "name": "*"
            },
            rootId: "root",
            rootLabel: "Library",
            childrenAttrs: ["items"]
        });

        repotree= new dijit.Tree({
            model: repomodel,
            dndController: "dijit.tree.dndSource",
            /*onClick: repoTreeOnClick,*/
            onDblClick: repoTreeOnDblClick,
            onClick:repoTreeOnClick
        },
        "mytree");
}









