pragma solidity ^0.5.4;

pragma solidity ^0.4.23;

contract BasicString{
    string private str;

    constructor() public{
        str = "Hello World";
    }

    function get() public view returns(string){
        return str;
    }

    function set(string _str) public{
        str = _str;
    }
}
